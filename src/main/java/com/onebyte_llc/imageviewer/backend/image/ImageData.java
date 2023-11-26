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
