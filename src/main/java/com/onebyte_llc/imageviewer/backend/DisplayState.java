package com.onebyte_llc.imageviewer.backend;

import com.onebyte_llc.imageviewer.controls.gridview.ImageRenderMode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DisplayState {

    // Image size is calculated from (gridBaseImageSize * scaleFactor)
    private final DoubleProperty gridBaseImageSize = new SimpleDoubleProperty();
    private final DoubleProperty gridMinScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridMaxScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridImageScaleFactor = new SimpleDoubleProperty();

    private final DoubleProperty fullScreenScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty fullScreeMinScaleFactor = new SimpleDoubleProperty(1);
    private final DoubleProperty fullScreeMaxScaleFactor = new SimpleDoubleProperty(5);

    private final ObjectProperty<ImageRenderMode> imageRenderMode = new SimpleObjectProperty<>();
    private final ObjectProperty<ImageHandle> fullScreenImage = new SimpleObjectProperty<>();

    public DisplayState() {
        gridBaseImageSize.setValue(50);
        gridMinScaleFactor.setValue(0.5);
        gridMaxScaleFactor.setValue(10);
        gridImageScaleFactor.setValue(1);
        imageRenderMode.setValue(ImageRenderMode.FIT);
    }

    public DoubleProperty gridBaseImageSizeProperty() {
        return gridBaseImageSize;
    }

    public DoubleProperty gridMinScaleFactorProperty() {
        return gridMinScaleFactor;
    }

    public DoubleProperty gridMaxScaleFactorProperty() {
        return gridMaxScaleFactor;
    }

    public DoubleProperty gridImageScaleFactorProperty() {
        return gridImageScaleFactor;
    }

    public ObjectProperty<ImageRenderMode> imageRenderModeProperty() {
        return imageRenderMode;
    }

    public ObjectProperty<ImageHandle> fullScreenImageProperty() {
        return fullScreenImage;
    }

    public DoubleProperty fullScreenScaleFactorProperty() {
        return fullScreenScaleFactor;
    }

    public DoubleProperty fullScreeMinScaleFactorProperty() {
        return fullScreeMinScaleFactor;
    }

    public DoubleProperty fullScreeMaxScaleFactorProperty() {
        return fullScreeMaxScaleFactor;
    }
}
