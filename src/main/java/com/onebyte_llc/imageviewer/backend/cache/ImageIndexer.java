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

import com.onebyte_llc.imageviewer.backend.ChangeSet;
import com.onebyte_llc.imageviewer.backend.ImageHandle;
import com.onebyte_llc.imageviewer.backend.db.Database;
import com.onebyte_llc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebyte_llc.imageviewer.backend.image.ImageData;
import com.onebyte_llc.imageviewer.backend.image.ImageLoader;
import com.onebyte_llc.imageviewer.backend.image.ImageTypeDefinition;
import com.onebyte_llc.imageviewer.collections.pool.Priority;
import com.onebyte_llc.imageviewer.collections.pool.PriorityThreadPool;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Streamable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * schedules index tasks and will
 * <ul>
 *     <li>Load source image from disk</li>
 *     <li>creates index/cached images of the source image and writes them to a cache dir</li>
 * </ul>
 */
public class ImageIndexer {

    private static final Logger LOG = Logger.getInstance(ImageIndexer.class);


    private final Streamable<ChangeSet<ImageHandle>> refreshRequest;
    private final List<ImageCacheDefinition> definitions;
    private final List<ImageTypeDefinition> imageLoaders;
    private final Database database;
    private final Path cachePath;
    private final PriorityThreadPool threadPool;
    private final FetchLock fetchLock = new FetchLock();
    private ImageCache imageCache;

    public ImageIndexer(List<ImageCacheDefinition> definitions, List<ImageTypeDefinition> imageLoaders,
                        Path cachePath, Database database, Streamable<ChangeSet<ImageHandle>> refreshRequest,
                        PriorityThreadPool threadPool) {

        this.definitions = new ArrayList<>(definitions);
        this.definitions.sort((o1, o2) -> Integer.compare(o2.getW() * o2.getH(), o1.getW() * o1.getH()));
        this.imageLoaders = imageLoaders;
        this.cachePath = cachePath;
        this.database = database;
        this.refreshRequest = refreshRequest;
        this.threadPool = threadPool;
    }

    public void setImageCache(ImageCache imageCache) {
        this.imageCache = imageCache;
    }

    public void asyncRemoveIndex(Priority priority, ImageRecord record) {
        threadPool.offer(priority, () -> {
            for (ImageCacheDefinition cacheDefinition : definitions) {
                String name = cacheDefinition.getFileName(record.getId() + "");
                Path path = cachePath.resolve(name);
                LOG.debug("Deleting index for {}", path);
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    LOG.error("Failed to delete {}", e, name);
                }
                database.deleteImageById(record.getId());
            }
            return true;
        });
    }

    public void asyncIndex(Priority priority, Path srcDir, ImageRecord record) {
        for (ImageTypeDefinition loader : imageLoaders) {
            if (loader.isLoadable(record.getFilename())) {
                asyncIndex(priority, record, loader.createLoader(srcDir.resolve(record.getFilename())));
                break;
            }
        }
    }

    public void asyncIndex(Priority priority, ImageRecord imageRecord, ImageLoader loader) {
        threadPool.offer(priority, () -> {
            if (!fetchLock.lock(imageRecord.getId())) {
                return true;
            }

            try {
                ImageRecord record = database.getImageById(imageRecord.getId());

                // read file data
                FileTime lastModified = Files.getLastModifiedTime(loader.getPath());
                ImageData data = null;

                // do we think we are already up to date?
                boolean upToDate = false;
                LocalDateTime fsTime = LocalDateTime.ofInstant(Files.getLastModifiedTime(loader.getPath())
                        .toInstant().truncatedTo(ChronoUnit.MILLIS), ZoneId.systemDefault());
                if (record.getFsModifyTime() != null && !fsTime.isAfter(record.getFsModifyTime().truncatedTo(ChronoUnit.MILLIS))) {
                    upToDate = true;
                }

                // write indexed images to disk
                // NOTE: This loop works backwards and requires sorted definitions from the largest definition
                // then scaling that image to the next largest, and so on, that way we
                // aren't always scaling the full size image, we are incrementally downscaling
                for (ImageCacheDefinition cacheDefinition : definitions) {
                    String name = cacheDefinition.getFileName(record.getId() + "");
                    Path path = cachePath.resolve(name);

                    // see if we can skip the index
                    if (Files.exists(path) && upToDate) {
                        continue;
                    }

                    // we load data here to try and skip loading if we don't need too
                    if (data == null) {
                        data = loader.readFromDisk();
                    }

                    // replace the larger image data with the new scaled image
                    // so the next down scale is using the previous downscale
                    data = indexImage(record, data, cacheDefinition);
                }

                if (data != null) {
                    // update database entry
                    record.setFsModifyTime(LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault()));
                    record.setImOriginalDate(data.getOriginalDate());
                    record.store();

                    // send an update event
                    // loader.getPath().getParent() is used because an ImageHandle wants the dir of the image record
                    // not the path of the actual image.
                    List<ImageHandle> handles = new ArrayList<>(1);
                    handles.add(new ImageHandle(imageCache, loader.getPath().getParent(), record));
                    refreshRequest.notify(new ChangeSet<>(false, null, handles, null));
                }
            } catch (IOException e) {
                LOG.error("Unable to create image index IOException: {}", e.getMessage());
            } catch (Exception e) {
                LOG.error("Unable to create image index: {}", e.getMessage());
            } finally {
                fetchLock.unlock(imageRecord.getId());
            }
            return true;
        });
    }

    private ImageData indexImage(ImageRecord record, ImageData data,
                                 ImageCacheDefinition cacheDefinition) throws IOException {

        LOG.debug("Indexing image {}", record.getId());

        double w = data.getImage().getWidth();
        double h = data.getImage().getHeight();

        double limW = cacheDefinition.getW();
        double limH = cacheDefinition.getH();
        if (w > limW) {
            double ratio = limW / w;
            w *= ratio;
            h *= ratio;
        }
        if (h > limH) {
            double ratio = limH / h;
            w *= ratio;
            h *= ratio;
        }

        // TODO - it would be good if we could read and builld the down sampled
        //  image without putting the original iin a BufferedImage  to save RAM.
        //  decode and progressively down sample an input stream
        BufferedImage resizedImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(data.getImage(), 0, 0, (int) w, (int) h, null);
        graphics2D.dispose();

        // save cache image
        Files.createDirectories(cachePath);
        String name = cacheDefinition.getFileName(record.getId() + "");
        File indexImg = new File(cachePath.resolve(name).toString());
        ImageIO.write(resizedImage, "jpg", indexImg);
        return data.cloneWithNewImage(resizedImage);
    }

}
