/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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

    protected void onAttach() {
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
