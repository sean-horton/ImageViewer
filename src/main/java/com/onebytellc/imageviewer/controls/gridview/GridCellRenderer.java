package com.onebytellc.imageviewer.controls.gridview;

import javafx.scene.canvas.GraphicsContext;

public interface GridCellRenderer<T> {
    void draw(T t, GraphicsContext gfx, double x, double y, double w, double h);
}
