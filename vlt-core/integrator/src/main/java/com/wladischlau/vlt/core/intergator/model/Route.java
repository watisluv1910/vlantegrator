package com.wladischlau.vlt.core.intergator.model;

import lombok.Data;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Route {

    private final Node startNode;
    private final Map<UUID, Node> nodes;
    private final List<Connection> connections;
    private final Graph<UUID, DefaultEdge> graphView;

    public Route(Set<Node> nodes, List<Connection> connections) {
        this.nodes = nodes.stream().collect(Collectors.toMap(Node::id, Function.identity()));
        this.connections = connections;
        this.graphView = formGraphView(connections);
        this.startNode = getStartNode(nodes, connections);
    }

    private static Graph<UUID, DefaultEdge> formGraphView(List<Connection> connections) {
        Graph<UUID, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);

        connections.forEach(c -> {
            if (!graph.containsVertex(c.fromNodeId())) {
                graph.addVertex(c.fromNodeId());
            }
            if (!graph.containsVertex(c.toNodeId())) {
                graph.addVertex(c.toNodeId());
            }
            graph.addEdge(c.fromNodeId(), c.toNodeId());
        });

        return graph;
    }

    private static Node getStartNode(Set<Node> nodes, List<Connection> connections) {
        var startNodes = nodes.stream()
                .filter(node -> connections.stream().noneMatch(it -> it.toNodeId().equals(node.id())))
                .filter(Objects::nonNull)
                .toList();

        if (startNodes.size() != 1) {
            throw new IllegalArgumentException("Ожидается ровно один стартовый узел, но найдено: " + startNodes.size());
        }

        return startNodes.getFirst();
    }
}
