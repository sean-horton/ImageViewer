package com.onebytellc.imageviewer.controls;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

public class ImageGridItem implements GridItem {

    private final Image image;

    public ImageGridItem(Image image) {
        this.image = image;
    }

    @Override
    public void draw(Canvas canvas, double x, double y, double w, double h) {
        double drawW = image.getWidth();
        double drawH = image.getHeight();

        if (drawW > w) {
            double ratio = w / drawW;
            drawW *= ratio;
            drawH *= ratio;
        }

        if (drawH > h) {
            double ratio = h / drawH;
            drawW *= ratio;
            drawH *= ratio;
        }

        canvas.getGraphicsContext2D().drawImage(image, x + ((w - drawW) / 2), y + ((h - drawH) / 2), drawW, drawH);
    }

}
