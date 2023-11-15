package com.onebyte_llc.imageviewer;

import javafx.scene.Node;

/**
 * @param <N> the node
 * @param <C> the
 */
public class ViewNode<N extends Node, C> {

    private N node;
    private C controller;


    public ViewNode(N node, C controller) {
        this.node = node;
        this.controller = controller;
    }

    public N getNode() {
        return node;
    }

    public C getController() {
        return controller;
    }
}
