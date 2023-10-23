package com.onebytellc.imageviewer.backend.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class ImageData {

    private final BufferedImage image;
    private final LocalDateTime originalDate;

    public ImageData(Builder builder) {
        this.image = builder.getImage();
        this.originalDate = builder.getOriginalDate();
    }

    public BufferedImage getImage() {
        return image;
    }

    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    public static class Builder {
        private BufferedImage image;
        private LocalDateTime originalDate;

        public BufferedImage getImage() {
            return image;
        }

        public Builder setImage(BufferedImage image) {
            this.image = image;
            return this;
        }

        public LocalDateTime getOriginalDate() {
            return originalDate;
        }

        public Builder setOriginalDate(LocalDateTime originalDate) {
            this.originalDate = originalDate;
            return this;
        }

        public ImageData build() {
            return new ImageData(this);
        }
    }
}
