package com.onebytellc.imageviewer.backend;

import com.onebytellc.imageviewer.logger.Logger;

public final class Context {

    private static final Logger LOG = Logger.getInstance(Context.class);

    private static Context INSTANCE;

    private final DisplayState displayState;

    private Context() {
        this.displayState = new DisplayState();
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
}
