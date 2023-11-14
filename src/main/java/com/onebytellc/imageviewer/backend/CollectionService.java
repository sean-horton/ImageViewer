package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.backend.cache.ImageCache;
import com.onebytellc.imageviewer.backend.cache.ImageIndexer;
import com.onebytellc.imageviewer.backend.cache.PriorityThreadPool;
import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionPathRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.CollectionRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.DirectoryRecord;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.ImageRecord;
import com.onebytellc.imageviewer.backend.explorer.ImageEvent;
import com.onebytellc.imageviewer.backend.explorer.ImageExplorer;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import com.onebytellc.imageviewer.logger.Logger;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Observable;
import com.onebytellc.imageviewer.reactive.Single;
import com.onebytellc.imageviewer.reactive.Streamable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manager over in image in-memory cache, image disk cache, and source disk images.
 * Tries to link all the pieces together for the UI.
 */
public class CollectionService {

    private static Logger LOG = Logger.getInstance(DisplayState.class);

    private final Database database;
    private final ImageExplorer explorer;
    private final ImageIndexer indexer;
    private final ImageCache imageCache;
    private final Streamable<ChangeSet<ImageHandle>> collectionImages;
    private final Streamable<Boolean> refreshRequest;

    private final ObservableList<CollectionRecord> collections = FXCollections.observableArrayList();
    private final ObjectProperty<CollectionRecord> collectionSelection = new SimpleObjectProperty<>();

    public CollectionService(Database database, ImageExplorer explorer, ImageIndexer indexer, ImageCache imageCache,
                             Streamable<ChangeSet<ImageHandle>> imageStreamable,
                             Streamable<Boolean> refreshRequest) {

        this.database = database;
        this.explorer = explorer;
        this.indexer = indexer;
        this.imageCache = imageCache;
        this.collectionImages = imageStreamable;
        this.refreshRequest = refreshRequest;

        collectionSelected().addListener((observable, oldValue, newValue) -> loadCollection(newValue));
        refreshCollection();
    }

    private void loadCollection(CollectionRecord record) {
        LOG.info("Loading collection {}: {}", record.getId(), record.getName());

        // notify UI to clear
        collectionImages.notify(new ChangeSet<>(true, null, null, null));

        // start fetching
        database.getPathsForCollection(record.getId())
                .observeOn(Executor.processThread())
                .subscribe(items -> {
                    explorer.reset();
                    for (CollectionPathRecord path : items) {
                        explorer.register(path.getDirectory(), path.getDepth())
                                .observeOn(Executor.processThread())
                                .subscribe(this::handleEvent);
                    }
                });
    }

    private void refreshCollection() {
        database.getCollections()
                .observeOn(Executor.fxApplicationThread())
                .subscribe(items -> {
                    collections.setAll(items.stream().toList());
                    if (collectionSelection.get() == null && !collections.isEmpty()) {
                        collectionSelection.setValue(collections.get(0));
                    }
                    if (collectionSelection.get() != null && !collections.isEmpty() && !collections.contains(collectionSelection.get())) {
                        collectionSelection.setValue(collections.get(0));
                    }
                });
    }

    public void deleteCollection(int collectionId) {
        LOG.info("Deleting collection {}", collectionId);
        // TODO - this also needs to delete any dangling directories and images
        //  and clean up the image cache
        database.deleteCollection(collectionId)
                .observeOn(Executor.processThread())
                .subscribe((i) -> refreshCollection());
    }

    public void updateCollection(CollectionRecord record) {
        database.updateCollection(record)
                .subscribe(e -> refreshCollection());
    }

    public ObservableList<CollectionRecord> collectionProperty() {
        return collections;
    }

    public ObjectProperty<CollectionRecord> collectionSelected() {
        return collectionSelection;
    }

    public Observable<Boolean> cacheUpdateStream() {
        return refreshRequest.observe();
    }

    public Observable<Boolean> addCollection(String name, int depth, String path) {
        return new Single<Boolean>()
                .subscribeOn(Executor.processThread())
                .onSubscribe((emitter) -> {
                    boolean res = database.addCollection(name, depth, path);
                    emitter.notify(res);
                    refreshCollection();
                })
                .observe();
    }

    /**
     * @return a stream of record events
     */
    public Observable<ChangeSet<ImageHandle>> collectionImageRecords() {
        return collectionImages.observe();
    }

    private void handleEvent(ImageEvent event) {
        try {
            DirectoryRecord directoryRecord = database.getOrAddDirectory(event.getRootDir().toString());
            List<ImageRecord> knownImages = database.getImagesInDirectory(directoryRecord);

            ChangeSet<ImageHandle> changeSet;
            switch (event.getType()) {
                case INIT -> changeSet = handleInit(directoryRecord, knownImages, event.getLoader());
                case ADDED -> changeSet = handleAdd(directoryRecord, knownImages, event.getLoader());
                case UPDATED -> changeSet = handleUpdate(directoryRecord, knownImages, event.getLoader());
                case REMOVED -> changeSet = handleDelete(directoryRecord, knownImages, event.getLoader());
                default -> throw new IllegalArgumentException("Unexpected argument type: " + event.getType());
            }
            collectionImages.notify(changeSet);
        } catch (Exception e) {
            LOG.error("Unable to handle image explorer event", e);
        }
    }

    private ChangeSet<ImageHandle> handleInit(DirectoryRecord directoryRecord,
                                              List<ImageRecord> knownImages,
                                              List<ImageLoader> availableImages) throws IOException {

        // Remove images that the DB is tracking that no longer exist in the file system
        Set<String> newImageFile = new HashSet<>(availableImages.stream().map(r ->
                r.getPath().getFileName().toString()).toList());
        Iterator<ImageRecord> iter = knownImages.iterator();
        while (iter.hasNext()) {
            ImageRecord knownImage = iter.next();
            if (!newImageFile.contains(knownImage.getFilename())) {
                LOG.info("remove file: {}", knownImage.getFilename());
                indexer.asyncRemoveIndex(PriorityThreadPool.Priority.LOW, knownImage);
                iter.remove();
            }
        }

        // Add/update images that are missing/outdated in the DB compared to file system
        Map<String, ImageRecord> knownImageFile = new HashMap<>();
        for (ImageRecord r : knownImages) {
            knownImageFile.put(r.getFilename(), r);
        }

        for (ImageLoader newImage : availableImages) {
            ImageRecord im = knownImageFile.get(newImage.getPath().getFileName().toString());
            if (im == null) {
                // ADDED
                LOG.info("add file: {}", newImage.getPath().getFileName());
                ImageRecord record = database.addImage(directoryRecord, newImage);
                knownImages.add(record);
                indexer.asyncIndex(PriorityThreadPool.Priority.MEDIUM, record, newImage);
            } else if (im.getFsModifyTime() == null) {
                FileTime fileLastModified = Files.getLastModifiedTime(newImage.getPath());
                LocalDateTime fileTime = LocalDateTime.ofInstant(fileLastModified.toInstant(), ZoneId.systemDefault());
                LocalDateTime savedTime = im.getFsModifyTime();

                if (savedTime != null && !fileTime.truncatedTo(ChronoUnit.MILLIS).isAfter(savedTime.truncatedTo(ChronoUnit.MILLIS))) {
                    continue;
                }

                // REINDEX
                LOG.info("reindex file: {}", newImage.getPath().getFileName());
                indexer.asyncIndex(PriorityThreadPool.Priority.MEDIUM, im, newImage);
            }
        }

        // NOTE: This is a init, so we want to add everything
        Path dir = Path.of(directoryRecord.getPath());
        List<ImageHandle> added = knownImages.stream()
                .map(m -> new ImageHandle(imageCache, dir, m))
                .toList();
        return new ChangeSet<>(false, added, null, null);
    }

    private ChangeSet<ImageHandle> handleAdd(DirectoryRecord directoryRecord,
                                             List<ImageRecord> knownImages,
                                             List<ImageLoader> addedImages) throws IOException {
        Path dir = Path.of(directoryRecord.getPath());
        List<ImageHandle> added = addedImages.stream()
                .map(m -> {
                    LOG.debug("Adding file: {}", m.getPath());
                    ImageRecord record = database.addImage(directoryRecord, m);
                    indexer.asyncIndex(PriorityThreadPool.Priority.MEDIUM, record, m);
                    return new ImageHandle(imageCache, dir, record);
                })
                .toList();
        return new ChangeSet<>(false, added, null, null);
    }


    private ChangeSet<ImageHandle> handleUpdate(DirectoryRecord directoryRecord,
                                                List<ImageRecord> knownImages,
                                                List<ImageLoader> updatedImages) throws IOException {
        Path dir = Path.of(directoryRecord.getPath());
        List<ImageHandle> updated = knownImages.stream()
                .map(m -> new ImageHandle(imageCache, dir, database.getImageById(m.getId())))
                .toList();
        return new ChangeSet<>(false, null, updated, null);
    }

    private ChangeSet<ImageHandle> handleDelete(DirectoryRecord directoryRecord,
                                                List<ImageRecord> knownImages,
                                                List<ImageLoader> removedImages) throws IOException {

        // delete records from DB
        List<ImageHandle> removed = new ArrayList<>(removedImages.size());
        for (ImageLoader r : removedImages) {
            for (ImageRecord record : knownImages) {
                if (r.getPath().getFileName().toString().equals(record.getFilename())) {
                    indexer.asyncRemoveIndex(PriorityThreadPool.Priority.LOW, record);
                    removed.add(new ImageHandle(imageCache, Path.of(directoryRecord.getPath()), record));
                    break;
                }
            }
        }

        return new ChangeSet<>(false, null, null, removed);
    }

}

