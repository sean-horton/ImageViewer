package com.onebytellc.imageviewer.backend.image;

import javafx.scene.image.Image;

import java.nio.file.Path;

public class ImageItem {

    private Path path;

    /**
     * Image may be null if it's not loaded yet.
     *
     * @param w desired width
     * @param h desired height
     * @return {@code null} if the image is not loaded yet, else the most appropriate image size will be returned
     */
    public Image getImage(double w, double h) {
        //return imageCache.get(path)
        return null;
    }

}
