package com.onebytellc.imageviewer.backend.image;

import com.onebytellc.imageviewer.reactive.Repeatable;
import javafx.scene.image.Image;

import java.nio.file.Path;

public class JpegImageTypeDefinition implements ImageTypeDefinition {

    public JpegImageTypeDefinition() {

    }

    @Override
    public ImageLoader createLoader(Path path) {
        return new JpegImageLoader(path);
    }

    @Override
    public boolean isLoadable(Path path) {
        String name = path.getFileName().toString();
        return name.endsWith(".jpg") || name.endsWith(".jpeg");
    }
}
