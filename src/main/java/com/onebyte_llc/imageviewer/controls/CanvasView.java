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

import javafx.animation.AnimationTimer;
import javafx.event.Event;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CanvasView extends AnchorPane {

    private final Canvas canvas;
    private final List<CanvasLayer> layers = new ArrayList<>();

    public CanvasView() {
        canvas = new Canvas();

        // anchor pane does not gain focus by default
        setFocusTraversable(true);
        setOnMouseEntered(event -> {
            event.consume();
            requestFocus();
            setFocused(true);
        });

        // allow anchor pane to resize
        setMinWidth(0);
        setMinHeight(0);

        // add the canvas to this AnchorPane
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        getChildren().add(canvas);

        // bind canvas size to this anchor pane
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        // redraw when the canvas is resized
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> draw());
        canvas.widthProperty().addListener((observable, oldValue, newValue) -> draw());

        // scroll listeners
        canvas.setOnScrollStarted(event -> handleEvent(event, (layer) -> layer.onScrollStarted(event)));
        canvas.setOnScroll(event -> handleEvent(event, (layer) -> layer.onScroll(event)));
        canvas.setOnScrollFinished(event -> handleEvent(event, (layer) -> layer.onScrollFinished(event)));

        // pinch to zoom listener
        canvas.setOnZoomStarted(event -> handleEvent(event, (layer) -> layer.onZoomStarted(event)));
        canvas.setOnZoom(event -> handleEvent(event, (layer) -> layer.onZoom(event)));
        canvas.setOnZoomFinished(event -> handleEvent(event, (layer) -> layer.onZoomFinished(event)));

        // click listener
        canvas.setOnMouseExited(event -> handleEvent(event, (layer -> layer.onMouseExited(event))));
        canvas.setOnMouseMoved(event -> handleEvent(event, (layer) -> layer.onMouseMoved(event)));
        canvas.setOnMouseClicked(event -> handleEvent(event, (layer) -> layer.onMouseClicked(event)));
        canvas.setOnMousePressed(event -> handleEvent(event, (layer) -> layer.onMousePressed(event)));
        canvas.setOnMouseReleased(event -> handleEvent(event, (layer) -> layer.onMouseReleased(event)));
        canvas.setOnMouseDragged(event -> handleEvent(event, (layer) -> layer.onMouseDragged(event)));
    }

    Canvas getCanvas() {
        return canvas;
    }

    private void handleEvent(Event event, EventHandler handler) {
        for (int i = layers.size() - 1; i >= 0; i--) {
            handler.handle(layers.get(i));
            if (event.isConsumed()) {
                draw();
                break;
            }
        }
    }

    public void invalidate() {
        draw();
    }

    private void draw() {
        for (CanvasLayer layer : layers) {
            layer.draw();
        }
    }


    public void attach(CanvasLayer layer) {
        if (layers.contains(layer)) {
            return; // TODO - maybe remove it and put on top?
        }

        layer.attach(this);
        layers.add(layer);
        layer.onAttach();
        invalidate();
    }

    public void detach(CanvasLayer layer) {
        if (layers.remove(layer)) {
            layer.onDetached();
        }
        invalidate();
    }

    public void playTransition(Transition transition, TimeUnit timeUnit, int time) {
        long durAsMilli = timeUnit.toNanos(time);
        long end = System.nanoTime() + durAsMilli;
        transition.onAttach(this);
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now >= end) {
                    this.stop();
                    transition.onTransitionComplete(CanvasView.this);
                } else {
                    transition.tick(canvas, (durAsMilli - (end - now)) / (double) durAsMilli);
                }
                invalidate();
            }
        }.start();
    }

    interface EventHandler {
        void handle(CanvasLayer layer);
    }

}
