package com.onebytellc.imageviewer.controls;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

public abstract class CanvasLayer {

    private CanvasView canvas;

    public abstract void draw();

    /////////////////////
    // package private
    void attach(CanvasView canvasView) {
        canvas = canvasView;
    }

    /////////////////////
    // Graphics
    protected void invalidate() {
        canvas.invalidate();
    }

    protected GraphicsContext getGraphics2D() {
        return canvas.getCanvas().getGraphicsContext2D();
    }

    public double getW() {
        return canvas.getWidth();
    }

    public double getH() {
        return canvas.getHeight();
    }


    //////////////////////////////
    // User Action (to override)
    protected void onKeyPressed(KeyEvent event) {
    }

    protected void onMouseClicked(MouseEvent event) {
    }

    protected void onMousePressed(MouseEvent event) {
    }

    protected void onMouseReleased(MouseEvent event) {
    }

    protected void onMouseDragged(MouseEvent event) {
    }


    protected void onScrollStarted(ScrollEvent event) {
    }

    protected void onScroll(ScrollEvent event) {
    }

    protected void onScrollFinished(ScrollEvent event) {
    }


    protected void onZoomStarted(ZoomEvent event) {
    }

    protected void onZoom(ZoomEvent event) {
    }

    protected void onZoomFinished(ZoomEvent event) {
    }
}
