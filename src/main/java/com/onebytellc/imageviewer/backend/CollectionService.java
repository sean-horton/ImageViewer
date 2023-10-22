package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.db.jooq.tables.records.PathRecord;
import com.onebytellc.imageviewer.backend.explorer.ImageExplorer;
import com.onebytellc.imageviewer.backend.image.ImageLoader;
import com.onebytellc.imageviewer.reactive.Executor;
import com.onebytellc.imageviewer.reactive.Observable;
import com.onebytellc.imageviewer.reactive.Repeatable;

import java.util.List;


public class CollectionService {

    private final Database database;
    private final ImageExplorer explorer;
    private Repeatable<List<ImageLoader>> collection = new Repeatable<>();

    public CollectionService(Database database, ImageExplorer explorer) {
        this.database = database;
        this.explorer = explorer;
    }

    public void addCollection() {

    }

    public void updateCollection() {

    }

    public void removeCollection() {

    }

    public Observable<List<ImageLoader>> selectCollection(int collectionId) {
        Executor.processThread().run(() -> {
            List<PathRecord> pathRecords = database.path(collectionId);

            for (PathRecord path : pathRecords) {
                explorer.register(path.getDirectory(), path.getDepth())
                        .observeOn(Executor.processThread())
                        .subscribe(load -> {

                        });
            }
        });

        // load collection from DB
        // start imageExplorer on collection
        // check diff, call any add/remove
        // update the collection data
        // fetch database contents and notify
        return null;
    }

}
