package com.onebytellc.imageviewer.backend.image;

import java.nio.file.Path;

public class JpegImageTypeDefinition implements ImageTypeDefinition {

    public JpegImageTypeDefinition() {

    }

    @Override
    public ImageLoader createLoader(Path path) {
        return new JpegImageLoader(path);
    }

    @Override
    public boolean isLoadable(String name) {
        return name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

}
