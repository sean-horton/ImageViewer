package com.onebytellc.imageviewer.backend.cache;

import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.collections.LRUCache;
import com.onebytellc.imageviewer.logger.Logger;
import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageCache {

    private static final Logger LOG = Logger.getInstance(ImageCache.class);

    private final List<CacheDef> cache;
    private final ImageIndexer imageIndexer;
    private final Path cacheDir;
    private final PriorityThreadPool threadPool;
    private final FetchLock fetchLock = new FetchLock();

    public ImageCache(List<ImageCacheDefinition> cacheDefinitions, ImageIndexer indexer,
                      Path cacheDir, PriorityThreadPool threadPool) {
        this.imageIndexer = indexer;
        this.cacheDir = cacheDir;
        this.threadPool = threadPool;

        // create a cache list that contains def and LRUCache
        cache = new ArrayList<>(cacheDefinitions.size());
        for (ImageCacheDefinition def : cacheDefinitions) {
            cache.add(new CacheDef(def, new LRUCache<>(def.getMaxCacheSize())));
        }

        // sort by smallest cache siize
        cache.sort(Comparator.comparingLong(o -> ((long) o.def.getW()) * ((long) o.def.getH())));
    }

    public Image asyncLoad(Path srcDir, ImageRecord imageRecord, int w, int h, ImageLoadCallback callback) {

        // find the appropriate cache definition
        CacheDef toUse = cache.get(0);
        for (CacheDef def : cache) {
            if (def.def.getW() >= w || def.def.getH() >= h) {
                toUse = def;
                break;
            }
        }

        // 1. is image in our cache, yes? return it
        Image image = toUse.cache.get(imageRecord.getId());
        if (image != null) {
            return image;
        }

        // If we made it this far we have to fetch from disk
        final CacheDef finalToUse = toUse;
        threadPool.offer(PriorityThreadPool.Priority.HIGH, () -> {
            try {
                if (!fetchLock.lock(imageRecord.getId())) {
                    // making sure we are only fetching the image once
                    return;
                }

                performLoad(srcDir, finalToUse, imageRecord);
            } catch (Exception e) {
                LOG.error("Failed to open image {}", e.getMessage());
            } finally {
                fetchLock.unlock(imageRecord.getId());
            }
        });

        // TODO - start thread
        // 2. Do we have a scheduled fetch? yes, block
        // 3. Start a fetch for specific thumbnail. Is it on disk? return it
        // 4. else do we have a scheduled image indexing operation? yes? return
        // 5. Start image indexer - it will create all image indexes
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
            Image image1 = new Image(new FileInputStream(srcDir.resolve(fileName).toString()));
            cacheDef.cache.put(record.getId(), image1);
        } else {
            String fileName = cacheDef.def.getFileName(record.getId() + "");
            Path path = cacheDir.resolve(fileName);

            if (Files.exists(path)) {
                Image image1 = new Image(new FileInputStream(path.toString()));
                cacheDef.cache.put(record.getId(), image1);
                // TODO - notify
                return;
            }

            imageIndexer.asyncIndex(PriorityThreadPool.Priority.MEDIUM, srcDir, record);
        }
    }

    private static class CacheDef {
        private final ImageCacheDefinition def;
        private final LRUCache<Integer, Image> cache;

        public CacheDef(ImageCacheDefinition cacheDefinitions, LRUCache<Integer, Image> cache) {
            this.def = cacheDefinitions;
            this.cache = cache;
        }
    }

    private static class FetchLock {
        private final Set<Integer> inProgress = new HashSet<>();

        public synchronized boolean lock(int key) {
            if (inProgress.contains(key)) {
                return false;
            }
            inProgress.add(key);
            return true;
        }

        public synchronized void unlock(int key) {
            inProgress.remove(key);
        }

    }

}
