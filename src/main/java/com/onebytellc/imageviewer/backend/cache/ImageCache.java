package com.onebytellc.imageviewer.backend.cache;

import java.util.List;
import java.util.Objects;

public class ImageCache {

    // TODO - we also need a background indexing process that starts indexing before
    //   this does loading...

    private final List<ImageCacheDefinition> cacheDefinitions;

    private final Cache<Long, Objects> cache = new Cache<>();

    public ImageCache(List<ImageCacheDefinition> cacheDefinitions) {
        this.cacheDefinitions = cacheDefinitions;
    }

    public void load(Long imageKey, int w, int h, ImageLoadCallback callback) {

        // 1. is image in our cache, yes? return it
        //     START THREAD
        // 2. Do we have a scheduled fetch? yes, block
        // 3. Start a fetch for specific thumbnail. Is it on disk? return it
        // 4. else do we have a scheduled image indexing operation? yes? return
        // 5. Start image indexer - it will create all image indexes

    }

    public void index(Long imageKey) {

    }

}
