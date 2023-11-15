package com.onebyte_llc.imageviewer.controls.gridview;

import com.onebyte_llc.imageviewer.backend.ImageHandle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.time.LocalDateTime;

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
    public void draw(ImageHandle handle, GraphicsContext gfx, int totalCellCount, double x, double y, double w, double h) {
        // TODO - This is a hacky/hardcoded way to get a SMALL, MEDIUM, and LARGE sized image.
        //   It should probably be an enum, but I changed how it worked later on and I'm lazy atm
        int s = totalCellCount == 1 ? Integer.MAX_VALUE : totalCellCount <= 100 ? 350 : 32;
        Image image = handle.getImage(s, s);
        if (image == null) {
            return;
        }

        double drawW = 0;
        double drawH = 0;

        double srcX = 0;
        double srcY = 0;
        double srcW = image.getWidth();
        double srcH = image.getHeight();

        if (renderMode == ImageRenderMode.FIT) {
            drawW = image.getWidth();
            drawH = image.getHeight();

            double ratio = w / drawW;
            drawW *= ratio;
            drawH *= ratio;

            if (drawH > h) {
                ratio = h / drawH;
                drawW *= ratio;
                drawH *= ratio;
            }

            x = x + ((w - drawW) / 2);
            y = y + ((h - drawH) / 2);
        } else if (renderMode == ImageRenderMode.FULL) {
            drawW = w;
            drawH = h;

            if (w / h > image.getWidth() / image.getHeight()) {
                double ratio = w / image.getWidth();
                double targetH = image.getHeight() * ratio;
                ratio = h / targetH;
                srcH = image.getHeight() * ratio;
                srcY = (image.getHeight() - srcH) / 2;
            } else {
                double ratio = h / image.getHeight();
                double targetW = image.getWidth() * ratio;
                ratio = w / targetW;
                srcW = image.getWidth() * ratio;
                srcX = (image.getWidth() - srcW) / 2;
            }
        }

        // draw the image
        gfx.drawImage(image,
                srcX,
                srcY,
                srcW,
                srcH,
                x + padding,
                y + padding,
                drawW - (padding * 2),
                drawH - (padding * 2));
    }

    @Override
    public LocalDateTime getDate(ImageHandle imageHandle) {
        return imageHandle.getImOriginalDate();
    }
}
