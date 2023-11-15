package com.onebyte_llc.imageviewer.controls;

import javafx.scene.canvas.Canvas;

public interface Transition {

    void onAttach(CanvasView canvasView);

    void onTransitionComplete(CanvasView canvasView);

    void tick(Canvas canvas, double percentComplete);

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
