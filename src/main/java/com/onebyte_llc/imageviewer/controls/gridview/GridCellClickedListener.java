package com.onebyte_llc.imageviewer.controls.gridview;

import javafx.geometry.Bounds;

public interface GridCellClickedListener<T> {
    void onClicked(T t, Bounds bounds);
}
