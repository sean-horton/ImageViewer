package com.onebytellc.imageviewer.backend.explorer;

import com.onebytellc.imageviewer.backend.image.ImageLoader;

public class ImageEvent {

    private final ImageLoader loader;
    private final ImageEventType type;

    public ImageEvent(ImageLoader loader, ImageEventType type) {
        this.loader = loader;
        this.type = type;
    }

    public ImageLoader getLoader() {
        return loader;
    }

    public ImageEventType getType() {
        return type;
    }
}
