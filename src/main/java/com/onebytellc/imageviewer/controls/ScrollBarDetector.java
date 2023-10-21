package com.onebytellc.imageviewer.controls;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public class ScrollBarDetector implements Detector {

    private ScrollBarListener listener;
    private Bounds bounds = new BoundingBox(0, 0, 0, 0);
    private MouseEvent start;
    private boolean isInGesture;
    private double offset;

    public void setOnScroll(ScrollBarListener listener) {
        this.listener = listener;
    }

    @Override
    public void setBound(Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public boolean onPressed(MouseEvent event) {
        if (bounds.contains(new Point2D(event.getX(), event.getY()))) {
            isInGesture = true;
            start = event;
            offset = start.getY() - bounds.getMinY();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseReleased(MouseEvent event) {
        return isInGesture;
    }

    @Override
    public boolean onMouseDragged(MouseEvent event) {
        listener.onMove(start, event, offset);
        return isInGesture;
    }

    @Override
    public boolean onMouseClicked(MouseEvent event) {
        if (isInGesture) {
            isInGesture = false;
            return true;
        }
        return false;
    }

    interface ScrollBarListener {
        void onMove(MouseEvent start, MouseEvent current, double offset);
    }
}
