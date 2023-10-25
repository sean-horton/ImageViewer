package com.onebytellc.imageviewer.backend.cache;

import java.util.HashSet;
import java.util.Set;

public class FetchLock {

    private final Set<Integer> inProgress = new HashSet<>();

    public synchronized boolean lock(int key) {
        if (inProgress.contains(key)) {
            return false;
        }
        inProgress.add(key);
        return true;
    }

    public synchronized void unlock(int key) {
        inProgress.remove(key);
    }

}
