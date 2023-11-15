package com.onebyte_llc.imageviewer.backend;

import java.util.ArrayList;
import java.util.List;

public class ChangeSet<T> {

    private final boolean reset;
    private final List<T> added;
    private final List<T> updated;
    private final List<T> removed;

    public ChangeSet(boolean reset, List<T> added, List<T> updated, List<T> removed) {
        this.reset = reset;
        this.added = added == null ? new ArrayList<>(0) : added;
        this.updated = updated == null ? new ArrayList<>(0) : updated;
        this.removed = removed == null ? new ArrayList<>(0) : removed;
    }

    public boolean isReset() {
        return reset;
    }

    public List<T> getAdded() {
        return added;
    }

    public List<T> getUpdated() {
        return updated;
    }

    public List<T> getRemoved() {
        return removed;
    }
}
