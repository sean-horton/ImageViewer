package com.onebytellc.imageviewer.backend.cache;

/**
 * Definition of cached images. Specify
 * <ul>
 *     <li>resolution of cached images on disk</li>
 *     <li>How many of these images we will store in RAM for in memory cache</li>
 * </ul>
 */
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

    // TODO - rotate cache directories based on file count?
    public String getFileName(String name) {
        return name + "_" + w + "x" + h + ".jpg";
    }
}
