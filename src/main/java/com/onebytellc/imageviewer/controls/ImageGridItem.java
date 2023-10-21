package com.onebytellc.imageviewer.controls;

import com.onebytellc.imageviewer.backend.image.ImageItem;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

public class ImageGridItem implements GridItem {

    private final ImageItem imageItem;

    public ImageGridItem(ImageItem imageItem) {
        this.imageItem = imageItem;
    }

    @Override
    public void draw(Canvas canvas, double x, double y, double w, double h) {
        Image image = imageItem.getImage(w, h);
        if (image == null) {
            return;
        }

        double drawW = image.getWidth();
        double drawH = image.getHeight();

        if (drawW < w) {
            double ratio = w / drawW;
            drawW *= ratio;
            drawH *= ratio;
        }

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
