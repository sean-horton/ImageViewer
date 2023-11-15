package com.onebyte_llc.imageviewer.backend;

import com.onebyte_llc.imageviewer.backend.cache.ImageCache;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import javafx.scene.image.Image;

import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * When ui calls load image on this class,
 * it will check the WeakReference, if that
 * is null it will fetch from cache and store in
 * weak reference
 */
public class ImageHandle {


    private final ImageCache cache;
    private final Path directory;
    private final ImageRecord imageRecord;
    private WeakReference<Image> image = new WeakReference<>(null);

    public ImageHandle(ImageCache cache, Path directory, ImageRecord imageRecord) {
        this.cache = cache;
        this.directory = directory;
        this.imageRecord = imageRecord;
    }

    public int getId() {
        return imageRecord.getId();
    }

    public String getFileName() {
        return imageRecord.getFilename();
    }

    public LocalDateTime getImOriginalDate() {
        return imageRecord.getImOriginalDate();
    }

    public Image getImage(double w, double h) {
        Image ref = image.get();

        if (ref == null || (w > ref.getWidth() || h > ref.getHeight())) {

            // if our reference is null OR our reference is smaller than requested size
            Image update = cache.asyncLoad(directory, imageRecord, (int) w, (int) h);

            if (update != null) {
                image = new WeakReference<>(update);
                return update;
            }
        }

        return ref;
    }

}
