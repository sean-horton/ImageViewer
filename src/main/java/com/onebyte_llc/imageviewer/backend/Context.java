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

package com.onebyte_llc.imageviewer.backend;

import com.onebyte_llc.imageviewer.backend.cache.ImageCache;
import com.onebyte_llc.imageviewer.backend.cache.ImageCacheDefinition;
import com.onebyte_llc.imageviewer.backend.cache.ImageIndexer;
import com.onebyte_llc.imageviewer.backend.db.Database;
import com.onebyte_llc.imageviewer.backend.explorer.ImageExplorer;
import com.onebyte_llc.imageviewer.backend.image.ImageTypeDefinition;
import com.onebyte_llc.imageviewer.backend.image.JpegImageTypeDefinition;
import com.onebyte_llc.imageviewer.backend.image.PngImageTypeDefinition;
import com.onebyte_llc.imageviewer.collections.pool.PriorityThreadPool;
import com.onebyte_llc.imageviewer.logger.Logger;
import com.onebyte_llc.imageviewer.reactive.Streamable;

import java.util.ArrayList;
import java.util.List;

public final class Context {

    private static final Logger LOG = Logger.getInstance(Context.class);

    private static Context INSTANCE;

    private final PriorityThreadPool threadPool;
    private final DisplayState displayState;
    private final ImageExplorer imageExplorer;
    private final ImageCache imageCache;

    private final Database database;
    private final ImageIndexer indexer;
    private final CollectionService collectionService;

    private Context(ContextParameters parameters) {
        // thread pool
        this.threadPool = new PriorityThreadPool("ImageCache", 6);

        // database
        this.database = new Database(Database.initialize(parameters.getDatabaseDir()));

        // searches for images in image collections
        List<ImageTypeDefinition> loaders = new ArrayList<>();
        loaders.add(new JpegImageTypeDefinition());
        loaders.add(new PngImageTypeDefinition());
        this.imageExplorer = new ImageExplorer(loaders);

        // ui state
        this.displayState = new DisplayState();

        // size definitions (indexer and cache share sizes, but cache also has full size) - 265mb of textures
        ImageCacheDefinition cacheSmall = new ImageCacheDefinition(64, 64, 4000);
        ImageCacheDefinition cacheMedium = new ImageCacheDefinition(500, 500, 200);
//        ImageCacheDefinition cacheFull = new ImageCacheDefinition(Integer.MAX_VALUE, Integer.MAX_VALUE, 5);

        // Streamable for notifying cache has updated
        Streamable<Boolean> refreshRequest = new Streamable<>();
        Streamable<ChangeSet<ImageHandle>> imageStreamable = new Streamable<>();

        // index image sizes (NOTE: we don't want to index full size images)
        List<ImageCacheDefinition> indexDef = new ArrayList<>(3);
        indexDef.add(cacheSmall);
        indexDef.add(cacheMedium);
        this.indexer = new ImageIndexer(indexDef, loaders, parameters.getImageCacheDir(), database, imageStreamable, threadPool);

        // cache definition
        List<ImageCacheDefinition> cacheDef = new ArrayList<>(3);
        cacheDef.add(cacheSmall);
        cacheDef.add(cacheMedium);
//        cacheDef.add(cacheFull);
        this.imageCache = new ImageCache(cacheDef, indexer, parameters.getImageCacheDir(), refreshRequest, threadPool);
        indexer.setImageCache(imageCache); // TODO - this is a code smell of looping dependencies, ImageIndexer <-> ImageCache

        // collection service
        this.collectionService = new CollectionService(database, imageExplorer, indexer, imageCache, imageStreamable, refreshRequest);
    }


    //////////////////////
    // static
    public static synchronized void initialize(ContextParameters parameters) {
        if (INSTANCE == null) {
            LOG.info("Context initialized");
            INSTANCE = new Context(parameters);
        } else {
            LOG.warn("Context was already created");
        }
    }

    public static synchronized void destroy() {
        if (INSTANCE == null) {
            return;
        }

        INSTANCE.imageExplorer.destroy();
    }

    public static Context getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Context must be initialized first!");
        }
        return INSTANCE;
    }


    //////////////////////
    // public
    public DisplayState getDisplayState() {
        return displayState;
    }

    public CollectionService getCollectionService() {
        return collectionService;
    }
}
