package com.onebytellc.imageviewer.controls.gridview;

import com.onebytellc.imageviewer.backend.ImageHandle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ImageGridRenderer implements GridCellRenderer<ImageHandle> {

    private ImageRenderMode renderMode = ImageRenderMode.FIT;
    private int padding = 1;

    public void setRenderMode(ImageRenderMode renderMode) {
        this.renderMode = renderMode;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

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

        if (renderMode == ImageRenderMode.FIT) {
            if (drawH > h) {
                ratio = h / drawH;
                drawW *= ratio;
                drawH *= ratio;
            }
        } else if (renderMode == ImageRenderMode.FULL) {
            if (drawH < h) {
                ratio = h / drawH;
                drawW *= ratio;
                drawH *= ratio;
            }
            if (h > drawH) {
                y -= (h - drawH) / 2;
            }
            if (w > drawW) {
                x -= (w - drawW) / 2;
            }
        }

        // TODO - macos has BAD clipping performance - disabling for now because it's terrible
        // save a non-clipped state
//        gfx.save();
//
//        // clip to draw bounds
//        gfx.beginPath();
//        gfx.rect(x + padding, y + padding, w - (padding * 2), h - (padding * 2));
//        gfx.closePath();
//        gfx.clip();

        // draw the image
        gfx.drawImage(image,
                x + ((w - drawW) / 2) + padding,
                y + ((h - drawH) / 2) + padding,
                drawW - (padding * 2),
                drawH - (padding * 2));

        // restore back to the non-clipped state
//        gfx.restore();
    }

}
