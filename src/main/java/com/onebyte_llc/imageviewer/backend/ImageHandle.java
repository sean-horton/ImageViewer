package com.onebyte_llc.imageviewer.backend;

import com.onebyte_llc.imageviewer.backend.cache.ImageCache;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebyte_llc.imageviewer.collections.pool.ScheduledTask;
import com.onebyte_llc.imageviewer.reactive.Executor;
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

    // original image reference holding
    private boolean obtainOriginal;
    private ScheduledTask<Image> originalImageTask;
    private Image originalImage;

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

    /**
     * Attempts to load the original source image. Since source images
     * may be large and can take a noticeable amount of time to prepare
     * the image texture, this may return an indexed image while async
     * load of the source. Be sure to call {@link #releaseOriginalImage()}
     * when done to free resources.
     *
     * @return an image
     */
    public Image obtainOriginalImage() {
        obtainOriginal = true;

        if (originalImage != null) {
            return originalImage;
        }
        if (originalImageTask != null && !originalImageTask.isComplete() && !originalImageTask.isCanceled()) {
            return getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        originalImageTask = cache.asyncLoadSourceImage(directory, imageRecord);
        originalImageTask.observe()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(image -> {
                    if (!obtainOriginal) {
                        return;
                    }

                    if (image.isError()) {
                        originalImage = getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
                    } else {
                        originalImage = image;
                    }
                });

        return getImage(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Release the original source image and any pending loads of the
     * original source image
     */
    public void releaseOriginalImage() {
        obtainOriginal = false;
        originalImage = null;
        if (originalImageTask != null) {
            originalImageTask.cancel();
            originalImageTask = null;
        }
    }


    /**
     * Get an indexed image that best matches the provided ui size
     *
     * @param w the ui width
     * @param h the ui height
     * @return the best matched indexed image for the provided size
     */
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
