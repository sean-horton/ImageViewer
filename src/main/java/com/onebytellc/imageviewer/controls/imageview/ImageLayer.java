package com.onebytellc.imageviewer.controls.imageview;

import com.onebytellc.imageviewer.backend.ImageHandle;
import com.onebytellc.imageviewer.controls.CanvasLayer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

public class ImageLayer extends CanvasLayer {

    // If for some reason this needs to draw a plain Image, make an interface
    // that has `getImage()` on it
    private final ObjectProperty<ImageHandle> imageProperty = new SimpleObjectProperty<>();

    public ImageLayer() {
        imageProperty.addListener((observable, oldValue, newValue) -> invalidate());
    }

    public ObjectProperty<ImageHandle> imagePropertyProperty() {
        return imageProperty;
    }

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

        GraphicsContext ctx = getGraphics2D();
        ctx.drawImage(image, 0, 0, w, h);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        // TODO - this will actually live in the controller class
        switch (event.getCode()) {
            case RIGHT, UP -> { // next

            }
            case LEFT, DOWN -> { // prev

            }
        }
    }
}
