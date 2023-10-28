package com.onebytellc.imageviewer.controls;

public interface Transition {

    CanvasLayer onTransitionComplete();

    static Transition leftToRight() {
        return null;
    }

    static Transition rightToLeft() {
        return null;
    }

    static Transition zoomExpand() {
        return null;
    }

    static Transition zoomCollapse() {
        return null;
    }

}
