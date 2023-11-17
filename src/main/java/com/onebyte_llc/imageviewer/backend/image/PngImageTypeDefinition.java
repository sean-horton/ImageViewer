package com.onebyte_llc.imageviewer.backend.image;

import java.nio.file.Path;

public class PngImageTypeDefinition implements ImageTypeDefinition {

    public PngImageTypeDefinition() {

    }

    @Override
    public ImageLoader createLoader(Path path) {
        return new PngImageLoader(path);
    }

    @Override
    public boolean isLoadable(String name) {
        return name.toLowerCase().endsWith(".png");
    }

}
