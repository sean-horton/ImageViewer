package com.onebytellc.imageviewer.controls;

import javafx.scene.canvas.Canvas;

public interface GridCell<T> {
    void draw(T t, Canvas canvas, double x, double y, double w, double h);
}
