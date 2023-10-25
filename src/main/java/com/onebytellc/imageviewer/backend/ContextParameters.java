package com.onebytellc.imageviewer.backend;

import java.nio.file.Path;

public class ContextParameters {

    private Path appDataPath;

    private ContextParameters(Builder builder) {
        this.appDataPath = builder.appDataPath;
    }

    public Path getDatabaseDir() {
        return appDataPath;
    }

    public Path getImageCacheDir() {
        return appDataPath.resolve("cache");
    }

    public static class Builder {

        private Path appDataPath;

        public Builder setAppDataPath(Path path) {
            this.appDataPath = path;
            return this;
        }

        public ContextParameters build() {
            return new ContextParameters(this);
        }

    }

}
