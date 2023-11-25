package com.onebyte_llc.imageviewer.collections.pool;

import com.onebyte_llc.imageviewer.reactive.Observable;
import com.onebyte_llc.imageviewer.reactive.Single;

public class ScheduledTask<T> {

    private final Priority priority;
    private final ScheduledRunnable<T> runnable;
    private final Single<T> single;

    private volatile boolean canceled;
    private volatile boolean complete;

    public ScheduledTask(Priority priority, ScheduledRunnable<T> runnable) {
        this.priority = priority;
        this.runnable = runnable;
        this.single = new Single<>();
    }

    void run() {
        if (complete) {
            throw new IllegalStateException("Can't run an already completed task!");
        }
        if (canceled) {
            return;
        }

        T data = runnable.run();
        complete = true;
        single.notify(data);
    }

    /////////////////////
    // PUBLIC
    public Priority getPriority() {
        return priority;
    }

    public Observable<T> observe() {
        return single.observe();
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public boolean isComplete() {
        return complete;
    }
}
