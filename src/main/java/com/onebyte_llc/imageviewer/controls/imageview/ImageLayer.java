/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.controls.imageview;

import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.controls.CanvasLayer;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ImageLayer extends CanvasLayer {

    // If for some reason this needs to draw a plain Image, make an interface
    // that has `getImage()` on it
    private final ObjectProperty<ImageHandle> imageProperty = new SimpleObjectProperty<>();

    private final DoubleProperty zoomScale = new SimpleDoubleProperty(1);
    private final DoubleProperty minZoomScale = new SimpleDoubleProperty(1);
    private final DoubleProperty maxZoomScale = new SimpleDoubleProperty(5);
    private final DoubleProperty offsetX = new SimpleDoubleProperty();
    private final DoubleProperty offsetY = new SimpleDoubleProperty();
    private final BooleanProperty slideshow = new SimpleBooleanProperty();

    // drag event state
    private MouseEvent startDragEvent;
    private double startDragOffsetX;
    private double startDragOffsetY;

    // slideshow
    private AnimationTimer slideshowAnimation;
    private ImageHandle slideshowPrevImage;
    private double slideshowAnimOffsetX;
    private double slideshowAnimOffsetY;
    private long slideshowNextFrameTime;

    public ImageLayer() {
        imageProperty.addListener((observable, oldValue, newValue) -> {
            // when the image changes, zoom back to regular full screen
            offsetX.setValue(0);
            offsetY.setValue(0);
            zoomScale.setValue(1);

            // lock original image texture and release previous one
            if (oldValue != null) {
                oldValue.releaseOriginalImage();
            }
            if (newValue != null) {
                newValue.obtainOriginalImage();
            }

            // and redraw
            invalidate();
        });
        zoomScale.addListener((observable, oldValue, newValue) -> draw());

        slideshowProperty().addListener((observable, oldValue, newValue) -> animateSlideshow(newValue));
    }

    private void animateSlideshow(boolean enable) {
        if (enable) {
            if (slideshowAnimation != null) {
                return; // already running
            }
            slideshowAnimation = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now < slideshowNextFrameTime) {
                        return;
                    }

                    if (slideshowPrevImage != imagePropertyProperty().get()) {
                        slideshowPrevImage = imageProperty.get();

                        Random r = new Random();
                        int low = -30;
                        int high = 30;
                        slideshowAnimOffsetX = r.nextInt(high - low) + low;
                        slideshowAnimOffsetY = r.nextInt(high - low) + low;

                        // TODO - set startPos, targetPos, and the time it has to perform the animation
                        zoomScale.setValue(1.1);
                        offsetX.setValue(slideshowAnimOffsetX);
                        offsetX.setValue(slideshowAnimOffsetY);
                    }

                    // TODO - animation could be improved, this is all random offsets and doesn't take into account
                    //   centering and being able to see the entire image
                    double move = 0.025;
                    offsetX.setValue(offsetX.get() + (slideshowAnimOffsetX > 0 ? move : -move));
                    offsetY.setValue(offsetY.get() + (slideshowAnimOffsetY > 0 ? move : -move));
                    invalidate();
                    slideshowNextFrameTime = now + TimeUnit.MILLISECONDS.toNanos(33);
                }
            };
            slideshowAnimation.start();
        } else {
            if (slideshowAnimation != null) {
                slideshowAnimation.stop();
                slideshowAnimation = null;
            }
            offsetY.setValue(0);
            offsetX.setValue(0);
            zoomScale.setValue(0);
            slideshowPrevImage = null;
        }
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

    public BooleanProperty slideshowProperty() {
        return slideshow;
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
        Image image = handle.obtainOriginalImage();

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

        Image image = handle.obtainOriginalImage();
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
