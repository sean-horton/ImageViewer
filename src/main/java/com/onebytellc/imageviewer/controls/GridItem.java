package com.onebytellc.imageviewer.controls;

import javafx.scene.canvas.Canvas;

public interface GridItem {
    void draw(Canvas canvas, double x, double y, double w, double h);
}
