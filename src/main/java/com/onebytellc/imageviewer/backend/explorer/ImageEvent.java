package com.onebytellc.imageviewer.backend.explorer;

import com.onebytellc.imageviewer.backend.image.ImageLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImageEvent {

    private final List<ImageLoader> loaders;
    private final ImageEventType type;
    private final Path rootDir;

    public ImageEvent(Path root, List<ImageLoader> loaders, ImageEventType type) {
        this.rootDir = root;
        this.loaders = loaders;
        this.type = type;
    }

    public ImageEvent(Path root, ImageLoader loader, ImageEventType type) {
        this.rootDir = root;
        this.loaders = new ArrayList<>(1);
        loaders.add(loader);
        this.type = type;
    }

    public List<ImageLoader> getLoader() {
        return loaders;
    }

    public ImageEventType getType() {
        return type;
    }

    public Path getRootDir() {
        return rootDir;
    }
}
