package com.onebyte_llc.imageviewer.backend.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class ImageData {

    private final BufferedImage image;
    private final LocalDateTime originalDate;
    private final Builder builder;

    private ImageData(Builder builder) {
        this.builder = builder;
        this.image = builder.getImage();
        this.originalDate = builder.getOriginalDate();
    }

    public BufferedImage getImage() {
        return image;
    }

    public LocalDateTime getOriginalDate() {
        return originalDate;
    }

    public ImageData cloneWithNewImage(BufferedImage bufferedImage) {
        return builder.setImage(bufferedImage).build();
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
