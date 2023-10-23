package com.onebytellc.imageviewer.backend.image;

public class ImageData {

    private final byte[] image;
    private final String originalDate;

    public ImageData(byte[] image, String originalDate) {
        this.image = image;
        this.originalDate = originalDate;
    }
}
