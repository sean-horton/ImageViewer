package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.logger.Logger;

public final class Context {

    private static final Logger LOG = Logger.getInstance(Context.class);

    private static Context INSTANCE;

    private Context() {

    }

    public static synchronized void initialize() {
        if (INSTANCE == null) {
            LOG.info("Context initialized");
            INSTANCE = new Context();
        } else {
            LOG.warn("Context was already created");
        }
    }

    public static Context getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Context must be initialized first!");
        }
        return INSTANCE;
    }

}
