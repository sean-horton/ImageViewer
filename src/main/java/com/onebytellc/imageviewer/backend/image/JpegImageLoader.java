package com.onebytellc.imageviewer.backend.image;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

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
    public ImageData readFromDisk() throws IOException {
        byte[] data = Files.readAllBytes(path);

        try {
            ImageData.Builder builder = new ImageData.Builder();

            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            builder.setImage(image);

            ImageMetadata metadata = Imaging.getMetadata(data);
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if (null != jpegMetadata) {
                final TiffImageMetadata exif = jpegMetadata.getExif();
                if (null != exif) {
                    String[] outputSet = exif.getFieldValue(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
                    if (outputSet != null) {
                        // TODO
//                        builder.setOriginalDate(LocalDateTime.parse(outputSet[0]));
                    }
                }
            }

            return builder.build();
        } catch (ImageReadException e) {
            throw new IOException("JPEG image could not be read", e);
        }
    }
}
