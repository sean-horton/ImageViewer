package com.onebytellc.imageviewer.controls.gridview;

import javafx.scene.canvas.GraphicsContext;

import java.time.LocalDateTime;

public interface GridCellRenderer<T> {

    void draw(T t, GraphicsContext gfx, int totalCellCount, double x, double y, double w, double h);

    LocalDateTime getDate(T t);

}
