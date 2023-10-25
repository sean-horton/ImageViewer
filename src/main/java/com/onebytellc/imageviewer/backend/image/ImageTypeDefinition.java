package com.onebytellc.imageviewer.backend.image;

import java.nio.file.Path;

public interface ImageTypeDefinition {

    ImageLoader createLoader(Path path);

    default boolean isLoadable(Path path) {
        return isLoadable(path.getFileName().toString());
    }

    boolean isLoadable(String path);

}
