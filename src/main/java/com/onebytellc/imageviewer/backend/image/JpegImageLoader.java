package com.onebytellc.imageviewer.backend.image;

import java.nio.file.Path;

public class JpegImageLoader implements ImageLoader {

    private final Path path;

    public JpegImageLoader(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ImageData readFromDisk() {
//        final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);

        return null;
    }
}
