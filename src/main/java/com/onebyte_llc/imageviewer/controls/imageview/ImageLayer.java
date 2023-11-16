package com.onebyte_llc.imageviewer.controls.imageview;

import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

public class ImageLayer extends CanvasLayer {

    // If for some reason this needs to draw a plain Image, make an interface
    // that has `getImage()` on it
    private final ObjectProperty<ImageHandle> imageProperty = new SimpleObjectProperty<>();

    private final DoubleProperty zoomScale = new SimpleDoubleProperty(1);
    private final DoubleProperty minZoomScale = new SimpleDoubleProperty(1);
    private final DoubleProperty maxZoomScale = new SimpleDoubleProperty(5);
    private final DoubleProperty offsetX = new SimpleDoubleProperty();
    private final DoubleProperty offsetY = new SimpleDoubleProperty();

    // drag event state
    private MouseEvent startDragEvent;
    private double startDragOffsetX;
    private double startDragOffsetY;


    public ImageLayer() {
        imageProperty.addListener((observable, oldValue, newValue) -> {
            // when the image changes, zoom back to regular full screen
            offsetX.setValue(0);
            offsetY.setValue(0);
            zoomScale.setValue(1);
            invalidate();
        });
        zoomScale.addListener((observable, oldValue, newValue) -> draw());
    }

    private double[] drawSize(Image image) {
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
        return new double[]{w * zoomScale.get(), h * zoomScale.get()};
    }

    /////////////////////
    // Properties
    public ObjectProperty<ImageHandle> imagePropertyProperty() {
        return imageProperty;
    }

    public DoubleProperty minZoomScaleProperty() {
        return minZoomScale;
    }

    public DoubleProperty maxZoomScaleProperty() {
        return maxZoomScale;
    }

    public DoubleProperty zoomScaleProperty() {
        return zoomScale;
    }


    /////////////////////
    // pinch to zoom
    @Override
    protected void onZoomStarted(ZoomEvent event) {
        event.consume();
    }

    @Override
    protected void onZoom(ZoomEvent event) {
        double newZoom = zoomScale.getValue() * event.getZoomFactor();
        newZoom = Math.min(Math.max(minZoomScale.get(), newZoom), maxZoomScale.get());
        zoomScale.set(newZoom);

        // make sure image is within bounds
        setOffset(offsetX.get() * event.getZoomFactor(), offsetY.get() * event.getZoomFactor());

        event.consume();
    }

    @Override
    protected void onZoomFinished(ZoomEvent event) {
        event.consume();
    }


    /////////////////////////
    // scroll to move around
    @Override
    protected void onScrollStarted(ScrollEvent event) {
        event.consume();
    }

    @Override
    protected void onScroll(ScrollEvent event) {


        // set the new position based on deltas
        double newX = offsetX.get() + event.getDeltaX();
        double newY = offsetY.get() + event.getDeltaY();

        setOffset(newX, newY);
        event.consume();
    }

    @Override
    protected void onScrollFinished(ScrollEvent event) {
        event.consume();
    }


    /////////////////
    // Dragging
    @Override
    protected void onMousePressed(MouseEvent event) {
        startDragOffsetX = offsetX.get();
        startDragOffsetY = offsetY.get();
        startDragEvent = event;
        event.consume();
    }

    @Override
    protected void onMouseDragged(MouseEvent event) {
        if (startDragEvent == null) {
            return;
        }

        double newX = startDragOffsetX + ((event.getX() - startDragEvent.getX()));
        double newY = startDragOffsetY + ((event.getY() - startDragEvent.getY()));

        setOffset(newX, newY);
        event.consume();
    }

    @Override
    protected void onMouseReleased(MouseEvent event) {
        event.consume();
    }

    private void setOffset(double newX, double newY) {
        ImageHandle handle = imageProperty.getValue();
        if (handle == null) {
            return;
        }
        Image image = handle.getImage(getW(), getH());

        // get draw image size
        double[] size = drawSize(image);
        double w = size[0];
        double h = size[1];

        // TODO - this needs to be improved
        // sanitize image drag bounds
        if (newX + w < getW()) {
            newX = getW() - w;
        }
        if (newX > 0) {
            newX = 0;
        }
        if (newY + h < getH()) {
            newY = getH() - h;
        }
        if (newY > 0) {
            newY = 0;
        }

        // set the values
        offsetX.setValue(newX);
        offsetY.setValue(newY);
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

        w *= zoomScale.get();
        h *= zoomScale.get();
        double x = getW() > w ? (getW() - w) / 2 : 0; // center image
        x += offsetX.get(); // scroll position
        x += getX();

        double y = getH() > h ? (getH() - h) / 2 : 0; // center the image
        y += offsetY.get(); // scroll position
        y += getY();

        GraphicsContext ctx = getGraphics2D();
        ctx.clearRect(0, 0, getW(), getH()); // improves performance?
        ctx.drawImage(image, x, y, w, h);
    }

}
