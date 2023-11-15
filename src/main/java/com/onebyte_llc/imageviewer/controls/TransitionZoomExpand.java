package com.onebyte_llc.imageviewer.controls;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.List;

public class TransitionZoomExpand implements Transition {

    private final Bounds startBounds;
    private final List<CanvasLayer> oldLayers;
    private final List<CanvasLayer> newLayers;
    private final CanvasLayer grayOpacityLayer;

    private double mostRecentPercent = 0;

    public TransitionZoomExpand(Bounds startBounds,
                                List<CanvasLayer> oldLayers, List<CanvasLayer> newLayers) {
        this.startBounds = startBounds;
        this.oldLayers = oldLayers;
        this.newLayers = newLayers;
        this.grayOpacityLayer = new CanvasLayer() {
            @Override
            public void draw() {
                getGraphics2D().setFill(new Color(0.0, 0.0, 0.0, mostRecentPercent));
                getGraphics2D().fillRect(0, 0, getW(), getH());
            }
        };
    }

    @Override
    public void onAttach(CanvasView canvasView) {
        canvasView.attach(grayOpacityLayer);
        for (CanvasLayer newLayer : newLayers) {
            // remove and add the new layer again to make sure they are correct order
            canvasView.detach(newLayer);
            newLayer.overrideBounds(startBounds);
            canvasView.attach(newLayer);
        }
    }

    @Override
    public void tick(Canvas canvas, double percentComplete) {
        mostRecentPercent = percentComplete;
        for (CanvasLayer newLayer : newLayers) {
            // resizing from start size to end size based on percent complete
            double x = (startBounds.getMinX()) * (1 - percentComplete);
            double y = (startBounds.getMinY()) * (1 - percentComplete);
            double w = ((canvas.getWidth() - startBounds.getWidth()) * percentComplete) + startBounds.getWidth();
            double h = ((canvas.getHeight() - startBounds.getHeight()) * percentComplete) + startBounds.getHeight();

            newLayer.overrideBounds(new BoundingBox(x, y, w, h));
        }
    }

    @Override
    public void onTransitionComplete(CanvasView canvasView) {
        canvasView.detach(grayOpacityLayer);
        for (CanvasLayer newLayer : newLayers) {
            newLayer.overrideBounds(null);
        }
        for (CanvasLayer oldLayer : oldLayers) {
            canvasView.detach(oldLayer);
        }
    }

}
