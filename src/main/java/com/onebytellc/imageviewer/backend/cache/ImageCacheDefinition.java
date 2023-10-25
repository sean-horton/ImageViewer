package com.onebytellc.imageviewer.backend.cache;

public class ImageCacheDefinition {

    private final int w;
    private final int h;
    private final int maxCacheSize;

    public ImageCacheDefinition(int w, int h, int maxCacheSize) {
        this.w = w;
        this.h = h;
        this.maxCacheSize = maxCacheSize;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public String getFileName(String name) {
        return name + "_" + w + "x" + h + ".jpg";
    }
}
