package com.onebytellc.imageviewer.backend.image;

import java.nio.file.Path;

public interface ImageTypeDefinition {

    ImageLoader createLoader(Path path);

    boolean isLoadable(Path path);

}
