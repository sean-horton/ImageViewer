package com.onebytellc.imageviewer.controls.gridview;

import com.onebytellc.imageviewer.backend.ImageHandle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ImageGridRenderer implements GridCellRenderer<ImageHandle> {

    @Override
    public void draw(ImageHandle handle, GraphicsContext gfx, double x, double y, double w, double h) {
        Image image = handle.getImage(w, h);
        if (image == null) {
            return;
        }

        double drawW = image.getWidth();
        double drawH = image.getHeight();

        double ratio = w / drawW;
        drawW *= ratio;
        drawH *= ratio;

        if (drawH > h) {
            ratio = h / drawH;
            drawW *= ratio;
            drawH *= ratio;
        }

        gfx.drawImage(image, x + ((w - drawW) / 2), y + ((h - drawH) / 2), drawW, drawH);
    }

}
