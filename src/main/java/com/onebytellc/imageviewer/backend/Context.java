package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.backend.cache.ImageCache;
import com.onebytellc.imageviewer.backend.cache.ImageCacheDefinition;
import com.onebytellc.imageviewer.backend.cache.compressor.ImageCompressor;
import com.onebytellc.imageviewer.backend.cache.compressor.JpegImageCompressor;
import com.onebytellc.imageviewer.backend.db.Database;
import com.onebytellc.imageviewer.backend.explorer.ImageExplorer;
import com.onebytellc.imageviewer.backend.image.ImageIndexer;
import com.onebytellc.imageviewer.backend.image.ImageTypeDefinition;
import com.onebytellc.imageviewer.backend.image.JpegImageTypeDefinition;
import com.onebytellc.imageviewer.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public final class Context {

    private static final Logger LOG = Logger.getInstance(Context.class);

    private static Context INSTANCE;

    private final DisplayState displayState;
    private final ImageExplorer imageExplorer;
    private final ImageCache imageCache;

    private final Database database;
    private final ImageIndexer indexer;
    private final CollectionService collectionService;

    private Context() {
        // database
        this.database = new Database(Database.initialize());

        // searches for images in image collections
        List<ImageTypeDefinition> loaders = new ArrayList<>();
        loaders.add(new JpegImageTypeDefinition());
        this.imageExplorer = new ImageExplorer(loaders);

        // ui state
        this.displayState = new DisplayState();

        // image indexed
        this.indexer = new ImageIndexer(10, database);

        // collection service
        this.collectionService = new CollectionService(database, imageExplorer, indexer);

        // cache definition
        ImageCompressor compressor = new JpegImageCompressor(70);
        List<ImageCacheDefinition> definitions = new ArrayList<>(3);
        definitions.add(new ImageCacheDefinition(compressor, 64, 64)); // 64x64
        definitions.add(new ImageCacheDefinition(compressor, 500, 500)); // 500x500
        definitions.add(new ImageCacheDefinition(compressor, Integer.MAX_VALUE, Integer.MAX_VALUE)); // FULL
        this.imageCache = new ImageCache(definitions);
    }


    //////////////////////
    // static
    public static synchronized void initialize() {
        if (INSTANCE == null) {
            LOG.info("Context initialized");
            INSTANCE = new Context();
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

    public ImageExplorer getImageExplorer() {
        return imageExplorer;
    }

    public ImageCache getImageCache() {
        return imageCache;
    }
}
