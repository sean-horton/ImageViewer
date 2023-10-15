package com.onebytellc.imageviewer.backend;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DisplayState {

    private final DoubleProperty cellSizeProperty = new SimpleDoubleProperty();

    public DisplayState() {
        cellSizeProperty.setValue(0);
    }

    public DoubleProperty cellSizeProperty() {
        return cellSizeProperty;
    }
}
