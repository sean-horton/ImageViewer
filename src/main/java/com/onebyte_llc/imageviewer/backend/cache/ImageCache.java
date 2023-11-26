/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.backend.cache;

import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebyte_llc.imageviewer.collections.LRUCache;
import com.onebyte_llc.imageviewer.collections.pool.Priority;
import com.onebyte_llc.imageviewer.collections.pool.PriorityThreadPool;
import com.onebyte_llc.imageviewer.collections.pool.ScheduledTask;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Streamable;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageCache {

    private static final Logger LOG = Logger.getInstance(ImageCache.class);

    private final Streamable<Boolean> refreshRequest;
    private final List<ImageIndexCache> cache;
    private final ImageIndexer imageIndexer;
    private final Path cacheDir;
    private final PriorityThreadPool threadPool;
    private final FetchLock fetchLock = new FetchLock();

    private final LRUCache<Integer, byte[]> fullScreenCache;


    public ImageCache(List<ImageCacheDefinition> cacheDefinitions, ImageIndexer indexer,
                      Path cacheDir, Streamable<Boolean> refreshRequest, PriorityThreadPool threadPool) {

        this.imageIndexer = indexer;
        this.cacheDir = cacheDir;
        this.refreshRequest = refreshRequest;
        this.threadPool = threadPool;
        this.fullScreenCache = new LRUCache<>(5);

        // create a cache list that contains def and LRUCache
        cache = new ArrayList<>(cacheDefinitions.size());
        for (ImageCacheDefinition def : cacheDefinitions) {
            cache.add(new ImageIndexCache(def, new LRUCache<>(def.getMaxCacheSize())));
        }

        // sort by smallest cache size
        cache.sort(Comparator.comparingLong(o -> ((long) o.def.getW()) * ((long) o.def.getH())));
    }

    /**
     * Loads indexed images.
     *
     * @param srcDir      the source
     * @param imageRecord
     * @param w
     * @param h
     * @return
     */
    public Image asyncLoad(Path srcDir, ImageRecord imageRecord, int w, int h) {

        // find the appropriate cache definition
        ImageIndexCache toUse = cache.get(cache.size() - 1);
        for (ImageIndexCache def : cache) {
            if (def.def.getW() >= w || def.def.getH() >= h) {
                toUse = def;
                break;
            }
        }

        // 1. is image in our cache, yes? return it
        Image item = toUse.cache.get(imageRecord.getId());
        if (item != null) {
            return item;
        }

        // If we made it this far we have to fetch from disk
        final ImageIndexCache finalToUse = toUse;
        threadPool.offer(Priority.HIGH, () -> {
            if (!fetchLock.lock(imageRecord.getId())) {
                // Do we have a scheduled fetch? yes,
                // making sure we are only fetching the image once
                return false;
            }

            try {
                // Start a fetch for specific thumbnail. Is it on disk? return it
                performLoad(srcDir, finalToUse, imageRecord);
            } catch (Exception e) {
                LOG.error("Failed to open image {}", e.getMessage());
            } finally {
                fetchLock.unlock(imageRecord.getId());
            }
            return true;
        });

        return null;
    }

    private void performLoad(Path srcDir, ImageIndexCache cacheDef, ImageRecord record) throws IOException {
        if (cacheDef.cache.contains(record.getId())) {
            return;
        }

        String fileName = cacheDef.def.getFileName(record.getId() + "");
        Path path = cacheDir.resolve(fileName);

        if (Files.exists(path)) {
            Image image1 = new Image(new FileInputStream(path.toString()));
            if (image1.isError()) {
                LOG.error("Image failed to load: {}", image1.exceptionProperty().get());
                refreshRequest.notify(true);
                return;
            }
            cacheDef.cache.put(record.getId(), image1);
            refreshRequest.notify(true);
            return;
        }

        // If we made it to this point we have to start an index
        imageIndexer.asyncIndex(Priority.MEDIUM, srcDir, record);
    }

    /**
     * Large full screen images may use a significant amount of RAM.
     * For example, an iPhone image in full res will use about 90mb.
     * For the full screen image cache we will store the bytes (about 5mb)
     * and only load it to a Image texture (90 mb) when it is needed.
     * Maybe we will move to this approach for all images eventually
     */
    public ScheduledTask<Image> asyncLoadSourceImage(Path srcDir, ImageRecord record) {
        return threadPool.offer(Priority.HIGH, () -> {
            LOG.debug("Loading full size image");
            try {
                String fileName = record.getFilename();

                // load bytes from cache
                byte[] bytes = fullScreenCache.get(record.getId());

                // get bytes from disk if needed
                if (bytes == null) {
                    bytes = Files.readAllBytes(srcDir.resolve(fileName));
                    fullScreenCache.put(record.getId(), bytes);
                }

                // convert byte cache to texture
                Image image = new Image(new ByteArrayInputStream(bytes));

                // TODO - this is a hack to make sure the image is published
                //  before the refresh notification goes out
                threadPool.offer(Priority.HIGH, () -> {
                    refreshRequest.notify(true);
                    return true;
                });

                return image;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Holds index image textures since they are small enough to fit in memory
     */
    private static class ImageIndexCache {
        private final ImageCacheDefinition def;
        private final LRUCache<Integer, Image> cache;

        public ImageIndexCache(ImageCacheDefinition cacheDefinitions, LRUCache<Integer, Image> cache) {
            this.def = cacheDefinitions;
            this.cache = cache;
        }
    }

}
