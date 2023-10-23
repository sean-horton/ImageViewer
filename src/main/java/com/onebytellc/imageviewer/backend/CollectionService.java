package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionPathRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.DirectoryRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.explorer.ImageEvent;
import com.onebytellc.imageviewer.backend.explorer.ImageExplorer;
import com.onebytellc.imageviewer.backend.image.ImageIndexer;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;

import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CollectionService {

    private static Logger LOG = Logger.getInstance(DisplayState.class);

    private final Database database;
    private final ImageExplorer explorer;
    private final ImageIndexer indexer;

    public CollectionService(Database database, ImageExplorer explorer, ImageIndexer indexer) {
        this.database = database;
        this.explorer = explorer;
        this.indexer = indexer;

        // TODO - remove
        explorer.register("/Users/shorton/imageviewtest", 10)
                .observeOn(Executor.processThread())
                .subscribe(this::handleEvent);
    }

    public void selectCollection(int collectionId) {
        //activeCollection.clear();
        Executor.processThread().run(() -> {
            List<CollectionPathRecord> pathRecords = database.getPathsForCollection(collectionId);

            for (CollectionPathRecord path : pathRecords) {
                explorer.register(path.getDirectory(), path.getDepth())
                        .observeOn(Executor.processThread())
                        .subscribe(this::handleEvent);
            }
        });
    }

    private void handleEvent(ImageEvent event) {
        try {
            switch (event.getType()) {
                case INIT -> initializeDirectory(event);
//            case ADDED -> {
//                for (ImageLoader loader : event.getLoader()) {
//                    activeCollection.add(new ImageGridItem(new ImageItem(loader)));
//                }
//            }
//            case REMOVED -> {
//                activeCollection.removeIf(item -> item.getPath().equals(event.getLoader().getPath()));
//            }
//            case UPDATED -> {
//                // TODO - iinvalidate cache
//                //  re-index
//            }
//            default -> {
//                throw new IllegalArgumentException("Unexpected argument type: " + event.getType());
//            }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage() + " " + e.getClass());

            Arrays.stream(e.getStackTrace()).forEach(r -> {
                LOG.error(r.toString());
            });
        }

        // TODO - sort the list
    }

    private void initializeDirectory(ImageEvent event) throws IOException {
        DirectoryRecord directoryRecord = database.getOrAddDirectory(event.getRootDir().toString());
        List<ImageRecord> knownImages = database.getImagesInDirectory(directoryRecord);
        List<ImageLoader> newImages = event.getLoader();

        Map<String, ImageRecord> knownImageFile = new HashMap<>();
        for (ImageRecord r : knownImages) {
            knownImageFile.put(r.getFilename(), r);
        }

        Set<String> newImageFile = new HashSet<>(newImages.stream().map(r -> r.getPath().getFileName().toString()).toList());

        for (ImageRecord knownImage : knownImages) {
            if (!newImageFile.contains(knownImage.getFilename())) {
                LOG.info("remove file: " + knownImage.getFilename());
                // TODO - remove
            }
        }

        for (ImageLoader newImage : newImages) {
            ImageRecord im = knownImageFile.get(newImage.getPath().getFileName().toString());
            if (im == null) {
                LOG.info("add file: " + newImage.getPath().getFileName());
                ImageRecord record = database.addImage(directoryRecord, newImage);
                knownImages.add(record);
                indexer.asyncIndex(ImageIndexer.Priority.MEDIUM, record, newImage);
            } else if (im.getFsModifyTime() == null ||
                    Files.getLastModifiedTime(newImage.getPath()).toInstant()
                            .isAfter(im.getFsModifyTime().toInstant(ZoneOffset.UTC))) {
                LOG.info("reindex file: " + newImage.getPath().getFileName());
                indexer.asyncIndex(ImageIndexer.Priority.MEDIUM, im, newImage);
            }
        }

//                    for (ImageLoader loader : event.getLoader()) {
//                        activeCollection.add(new ImageGridItem(new ImageItem(loader)));
//                    }
    }
}
