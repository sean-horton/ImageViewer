package com.onebyte_llc.imageviewer.backend.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PngImageLoader implements ImageLoader {

    private final Path path;

    public PngImageLoader(Path path) {
        this.path = path;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ImageData readFromDisk() throws IOException {
        byte[] data = Files.readAllBytes(path);

        ImageData.Builder builder = new ImageData.Builder();

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
        builder.setImage(image);

        // TODO - newer versions of PNG may have exif data, but
        //  apache imaging doesn't really support that right now

        return builder.build();

    }
}
