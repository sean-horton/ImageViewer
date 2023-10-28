package com.onebytellc.imageviewer.controls.gridview;

import javafx.geometry.Bounds;

public interface GridCellClickedListener<T> {
    void onClicked(T t, Bounds bounds);
}
