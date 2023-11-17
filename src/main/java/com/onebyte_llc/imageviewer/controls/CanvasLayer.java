package com.onebyte_llc.imageviewer.controls;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

public abstract class CanvasLayer {

    private CanvasView canvas;
    private Bounds bounds;

    public abstract void draw();

    /////////////////////
    // package private
    void attach(CanvasView canvasView) {
        canvas = canvasView;
    }

    void overrideBounds(Bounds bounds) {
        this.bounds = bounds;
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
        return bounds == null ? canvas.getWidth() : bounds.getWidth();
    }

    public double getH() {
        return bounds == null ? canvas.getHeight() : bounds.getHeight();
    }

    public double getX() {
        return bounds == null ? 0 : bounds.getMinX();
    }

    public double getY() {
        return bounds == null ? 0 : bounds.getMinY();
    }


    //////////////////////////////
    // Callbacks
    protected void onDetached() {
    }


    //////////////////////////////
    // User Action (to override)
    protected void onKeyPressed(KeyEvent event) {
    }

    protected void onMouseMoved(MouseEvent event) {
    }

    protected void onMouseExited(MouseEvent event) {
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
