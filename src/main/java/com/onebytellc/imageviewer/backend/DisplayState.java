package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.reactive.Observable;
import com.onebytellc.imageviewer.reactive.Subscriber;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;

import java.util.List;

public class DisplayState {


    // Image size is calculated from (DEFAULT_IMAGE_SIZE * scaleFactor)
    private final DoubleProperty gridBaseImageSize = new SimpleDoubleProperty();
    private final DoubleProperty gridMinScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridMaxScaleFactor = new SimpleDoubleProperty();
    private final DoubleProperty gridImageScaleFactor = new SimpleDoubleProperty();

    // Active image collection
    private final Subscriber<List<Image>> activeCollection = new Subscriber<>();

    public DisplayState() {
        gridBaseImageSize.setValue(50);
        gridMinScaleFactor.setValue(0.5);
        gridMaxScaleFactor.setValue(10);
        gridImageScaleFactor.setValue(1);
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

    public void setImages(List<Image> images) {
        activeCollection.notify(images);
    }

    public Observable<List<Image>> observeActiveImages() {
        return activeCollection.observe();
    }

}
