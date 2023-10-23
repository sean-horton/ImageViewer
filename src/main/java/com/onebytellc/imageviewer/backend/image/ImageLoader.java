package com.onebytellc.imageviewer.backend.image;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageLoader {

    Path getPath();

    ImageData readFromDisk() throws IOException;

}
