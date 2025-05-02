package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.core.integrator.model.Branch;
import com.wladischlau.vlt.core.integrator.model.Node;
import com.wladischlau.vlt.core.integrator.model.Route;
import org.jgrapht.Graph;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchExtractor {

    /**
     * Извлекает ветви из маршрута на основе графа и сопоставления узлов.
     * <p>
     * Граничными считаются узлы, у которых выполняется хотя бы одно из условий:
     * <ul>
     *     <li>{@code in degree == 0} (стартовый узел).</li>
     *     <li>{@code out degree == 0} (конечный узел).</li>
     *     <li>{@code out degree > 1} (разветвляющийся узел).</li>
     * </ul>
     * <p>
     * Для каждого граничного узла, имеющего исходящие ребра, для каждого исходящего ребра строится ветвь – начиная с
     * текущего узла и далее по цепочке, пока не встретится следующий граничный узел.
     *
     * @param route объект маршрута, содержащий граф {@link Route#getGraphView()} и сопоставление узлов
     *              {@link Route#getNodes()}.
     *
     * @return список ветвей, каждая ветвь – список узлов.
     */
    public List<Branch> extractBranches(Route route) {
        if (route.getNodes().size() == 1) {
            Node single = route.getNodes().values().stream().findFirst().get();
            return Collections.singletonList(new Branch(single));
        }

        var graph = route.getGraphView();

        var boundaries = graph.vertexSet().stream()
                .filter(vtx -> isBoundary(graph, vtx))
                .collect(Collectors.toSet());

        var branches = new ArrayList<Branch>();

        boundaries.stream()
                // Не учитываются граничные узлы, у которых нет исходящих ребер
                .filter(it -> !graph.outgoingEdgesOf(it).isEmpty())
                // Для каждого граничного узла с исходящими ребрами
                .forEach(boundary -> {
                    var outEdges = graph.outgoingEdgesOf(boundary);

                    var boundaryNode = route.getNodes().get(boundary);
                    if (graph.incomingEdgesOf(boundary).isEmpty() && outEdges.size() > 1) {
                        branches.add(new Branch(boundaryNode));
                    }

                    outEdges.forEach(outEdge -> {
                        var branch = new Branch(boundaryNode, new ArrayList<>());

                        var curr = graph.getEdgeTarget(outEdge);
                        while (!boundaries.contains(curr)) {
                            branch.addNode(route.getNodes().get(curr));
                            var currOutEdges = graph.outgoingEdgesOf(curr);
                            if (currOutEdges.size() != 1) {
                                break;
                            }
                            curr = graph.getEdgeTarget(currOutEdges.iterator().next());
                        }

                        // Добавление конечного граничного узла (если он еще не добавлен)
                        if (branch.getNodes().isEmpty() || !branch.getNodes().getLast().id().equals(curr)) {
                            branch.addNode(route.getNodes().get(curr));
                        }

                        branches.add(branch);
                    });
                });

        return branches;
    }

    private static <V, E> boolean isBoundary(Graph<V, E> graph, V vertex) {
        return graph.inDegreeOf(vertex) == 0 || graph.outDegreeOf(vertex) == 0 || graph.outDegreeOf(vertex) > 1;
    }
}