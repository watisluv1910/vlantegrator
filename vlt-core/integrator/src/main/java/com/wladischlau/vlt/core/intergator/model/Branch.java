package com.wladischlau.vlt.core.intergator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public final class Branch {

    private Node parent;
    private List<Node> nodes;

    public Branch(Node node) {
        this(Collections.singletonList(node));
    }

    public Branch(List<Node> nodes) {
        this(null, nodes);
    }

    public void addNode(Node node) {
        if (nodes == null) {
            setNodes(new ArrayList<>());
        }
        nodes.add(node);
    }
}