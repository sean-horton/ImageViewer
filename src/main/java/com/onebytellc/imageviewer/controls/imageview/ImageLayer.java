package com.onebytellc.imageviewer.controls.imageview;

import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

public class ImageLayer extends CanvasLayer {

    // If for some reason this needs to draw a plain Image, make an interface
    // that has `getImage()` on it
    private final ObjectProperty<ImageHandle> imageProperty = new SimpleObjectProperty<>();

    private final DoubleProperty zoomScale = new SimpleDoubleProperty(1);
    private final DoubleProperty maxZoomScale = new SimpleDoubleProperty(5);
    private final DoubleProperty offsetX = new SimpleDoubleProperty();
    private final DoubleProperty offsetY = new SimpleDoubleProperty();


    public ImageLayer() {
        imageProperty.addListener((observable, oldValue, newValue) -> {
            // when the image changes, zoom back to regular full screen
            offsetX.setValue(0);
            offsetY.setValue(0);
            zoomScale.setValue(1);
            invalidate();
        });
    }

    /////////////////////
    // Properties
    public ObjectProperty<ImageHandle> imagePropertyProperty() {
        return imageProperty;
    }

    public DoubleProperty maxZoomScaleProperty() {
        return maxZoomScale;
    }


    /////////////////////
    // User Action
    @Override
    protected void onZoomStarted(ZoomEvent event) {
        event.consume();
    }

    @Override
    protected void onZoom(ZoomEvent event) {
        double newZoom = zoomScale.getValue() * event.getZoomFactor();
        newZoom = Math.min(Math.max(1, newZoom), maxZoomScale.get());
        zoomScale.set(newZoom);
        event.consume();
    }

    @Override
    protected void onZoomFinished(ZoomEvent event) {
        event.consume();
    }


    @Override
    protected void onScrollStarted(ScrollEvent event) {
        event.consume();
    }

    @Override
    protected void onScroll(ScrollEvent event) {
        offsetX.setValue(offsetX.get() + event.getDeltaX());
        offsetY.setValue(offsetY.get() + event.getDeltaY());
        event.consume();
    }

    @Override
    protected void onScrollFinished(ScrollEvent event) {
        event.consume();
    }

    /////////////////////
    // Render
    @Override
    public void draw() {
        ImageHandle handle = imageProperty.getValue();
        if (handle == null) {
            return;
        }

        Image image = handle.getImage(getW(), getH());
        if (image == null) {
            return;
        }

        // resize
        double w = image.getWidth();
        double h = image.getHeight();

        double ratio = getW() / w;
        w *= ratio;
        h *= ratio;

        if (h > getH()) {
            ratio = getH() / h;
            w *= ratio;
            h *= ratio;
        }

        double x = (getW() - w) / 2; // center image
        x += offsetX.get(); // scroll position
        x += getX();

        double y = (getH() - h) / 2; // center the image
        y += offsetY.get(); // scroll position
        y += getY();

        GraphicsContext ctx = getGraphics2D();
        ctx.drawImage(image, x, y, w * zoomScale.get(), h * zoomScale.get());
    }

}
