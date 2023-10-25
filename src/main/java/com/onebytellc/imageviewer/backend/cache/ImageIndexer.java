package com.onebytellc.imageviewer.backend.cache;

import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.image.ImageData;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import com.onebytellc.imageviewer.backend.image.ImageTypeDefinition;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Streamable;

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
import java.util.List;

public class ImageIndexer {

    private static final Logger LOG = Logger.getInstance(ImageIndexer.class);


    private final Streamable<Boolean> refreshRequest;
    private final List<ImageCacheDefinition> definitions;
    private final List<ImageTypeDefinition> imageLoaders;
    private final Database database;
    private final Path cachePath;
    private final PriorityThreadPool threadPool;
    private final FetchLock fetchLock = new FetchLock();

    public ImageIndexer(List<ImageCacheDefinition> definitions, List<ImageTypeDefinition> imageLoaders,
                        Path cachePath, Database database, Streamable<Boolean> refreshRequest,
                        PriorityThreadPool threadPool) {

        this.definitions = definitions;
        this.imageLoaders = imageLoaders;
        this.cachePath = cachePath;
        this.database = database;
        this.refreshRequest = refreshRequest;
        this.threadPool = threadPool;
    }

    public void asyncRemoveIndex(PriorityThreadPool.Priority priority, ImageRecord record) {
        threadPool.offer(priority, () -> {
            // TODO - remove indexed image
            // TODO - delete record from DB
        });
    }

    public void asyncIndex(PriorityThreadPool.Priority priority, Path srcDir, ImageRecord record) {
        for (ImageTypeDefinition loader : imageLoaders) {
            if (loader.isLoadable(record.getFilename())) {
                asyncIndex(priority, record, loader.createLoader(srcDir.resolve(record.getFilename())));
                break;
            }
        }
    }

    public void asyncIndex(PriorityThreadPool.Priority priority, ImageRecord imageRecord, ImageLoader loader) {
        threadPool.offer(priority, () -> {
            if (!fetchLock.lock(imageRecord.getId())) {
                return;
            }

            try {
                ImageRecord record = database.getImageById(imageRecord.getId());

                // read file data
                FileTime lastModified = Files.getLastModifiedTime(loader.getPath());
                ImageData data = loader.readFromDisk();

                // write indexed images to disk
                for (ImageCacheDefinition cacheDefinition : definitions) {
                    indexImage(record, data, cacheDefinition);
                }

                // update database entry
                record.setFsModifyTime(LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault()));
                record.store();

                refreshRequest.notify(true);
            } catch (IOException e) {
                LOG.error("Unable to create image index IOException: {}", e.getMessage());
            } catch (Exception e) {
                LOG.error("Unable to create image index: {}", e.getMessage());
            } finally {
                fetchLock.unlock(imageRecord.getId());
            }
        });
    }

    private void indexImage(ImageRecord record, ImageData data, ImageCacheDefinition cacheDefinition) throws IOException {
        String name = cacheDefinition.getFileName(record.getId() + "");
        Path path = cachePath.resolve(name);

        // see if we already have this indexed
        if (Files.exists(path)) {
            LocalDateTime fsTime = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant()
                    .truncatedTo(ChronoUnit.MILLIS), ZoneId.systemDefault());
            LocalDateTime savedTime = record.getFsModifyTime().truncatedTo(ChronoUnit.MILLIS);
            if (!fsTime.isAfter(savedTime)) {
                return;
            }
        }
        
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

        BufferedImage resizedImage = new BufferedImage((int) w, (int) h, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(data.getImage(), 0, 0, (int) w, (int) h, null);
        graphics2D.dispose();

        File indexImg = new File(path.toString());
        ImageIO.write(resizedImage, "jpg", indexImg);
    }

}
