package com.onebyte_llc.imageviewer.backend.cache;

import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebyte_llc.imageviewer.collections.LRUCache;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Streamable;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageCache {

    private static final Logger LOG = Logger.getInstance(ImageCache.class);

    private final Streamable<Boolean> refreshRequest;
    private final List<CacheDef> cache;
    private final ImageIndexer imageIndexer;
    private final Path cacheDir;
    private final PriorityThreadPool threadPool;
    private final FetchLock fetchLock = new FetchLock();

    public ImageCache(List<ImageCacheDefinition> cacheDefinitions, ImageIndexer indexer,
                      Path cacheDir, Streamable<Boolean> refreshRequest, PriorityThreadPool threadPool) {

        this.imageIndexer = indexer;
        this.cacheDir = cacheDir;
        this.refreshRequest = refreshRequest;
        this.threadPool = threadPool;

        // create a cache list that contains def and LRUCache
        cache = new ArrayList<>(cacheDefinitions.size());
        for (ImageCacheDefinition def : cacheDefinitions) {
            cache.add(new CacheDef(def, new LRUCache<>(def.getMaxCacheSize())));
        }

        // sort by smallest cache siize
        cache.sort(Comparator.comparingLong(o -> ((long) o.def.getW()) * ((long) o.def.getH())));
    }

    public Image asyncLoad(Path srcDir, ImageRecord imageRecord, int w, int h) {

        // find the appropriate cache definition
        CacheDef toUse = cache.get(0);
        for (CacheDef def : cache) {
            if (def.def.getW() >= w || def.def.getH() >= h) {
                toUse = def;
                break;
            }
        }

        // 1. is image in our cache, yes? return it
        ImageCacheItem item = toUse.cache.get(imageRecord.getId());
        if (item != null) {
            return item.get();
        }

        // If we made it this far we have to fetch from disk
        final CacheDef finalToUse = toUse;
        threadPool.offer(PriorityThreadPool.Priority.HIGH, () -> {
            if (!fetchLock.lock(imageRecord.getId())) {
                // Do we have a scheduled fetch? yes,
                // making sure we are only fetching the image once
                return;
            }

            try {
                // Start a fetch for specific thumbnail. Is it on disk? return it
                performLoad(srcDir, finalToUse, imageRecord);
            } catch (Exception e) {
                LOG.error("Failed to open image {}", e.getMessage());
            } finally {
                fetchLock.unlock(imageRecord.getId());
            }
        });

        return null;
    }

    private void performLoad(Path srcDir, CacheDef cacheDef, ImageRecord record) throws IOException {
        if (cacheDef.cache.contains(record.getId())) {
            return;
        }

        if (cacheDef.def.getW() == Integer.MAX_VALUE) {
            // load actual file
            LOG.debug("Loading full size image");
            String fileName = record.getFilename();
            cacheDef.cache.put(record.getId(), new FullScreenCache(Files.readAllBytes(srcDir.resolve(fileName))));
            refreshRequest.notify(true);
        } else {
            String fileName = cacheDef.def.getFileName(record.getId() + "");
            Path path = cacheDir.resolve(fileName);

            if (Files.exists(path)) {
                Image image1 = new Image(new FileInputStream(path.toString()));
                if (image1.isError()) {
                    LOG.error("Image failed to load: {}", image1.exceptionProperty().get());
                    refreshRequest.notify(true);
                    return;
                }
                cacheDef.cache.put(record.getId(), new IndexImageCache(image1));
                refreshRequest.notify(true);
                return;
            }

            // If we made it to this point we have to start an index
            imageIndexer.asyncIndex(PriorityThreadPool.Priority.MEDIUM, srcDir, record);
        }
    }

    private static class CacheDef {
        private final ImageCacheDefinition def;
        private final LRUCache<Integer, ImageCacheItem> cache;

        public CacheDef(ImageCacheDefinition cacheDefinitions, LRUCache<Integer, ImageCacheItem> cache) {
            this.def = cacheDefinitions;
            this.cache = cache;
        }
    }

    private interface ImageCacheItem {
        Image get();
    }

    /**
     * For smaller indexing images we will store the Image directly in memory
     * as the full screen image texture is not so large memory wise. SSee {@link FullScreenCache}
     * for a better explanation
     */
    private static class IndexImageCache implements ImageCacheItem {
        private Image image;

        public IndexImageCache(Image image) {
            this.image = image;
        }

        public Image get() {
            return image;
        }
    }

    /**
     * Large full screen images may use a significant amount of RAM.
     * For example, an iPhone image in full res will use about 90mb.
     * For the full screen image cache we will store the bytes (about 5mb)
     * and only load it to a Image texture (90 mb) when it is needed.
     */
    private static class FullScreenCache implements ImageCacheItem {
        private final byte[] bytes;
        private WeakReference<Image> image = new WeakReference<>(null);

        public FullScreenCache(byte[] bytes) {
            this.bytes = bytes;
        }

        public Image get() {
            Image ret = image.get();
            if (ret == null) {
                ret = new Image(new ByteArrayInputStream(bytes));
                image = new WeakReference<>(ret);
            }
            if (ret.isError()) {
                LOG.error("Failed to load full screen image: {}", ret.exceptionProperty().get());
                return null;
            }
            return ret;
        }

    }

}
