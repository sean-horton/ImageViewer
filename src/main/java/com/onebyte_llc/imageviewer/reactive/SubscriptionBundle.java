package com.onebyte_llc.imageviewer.reactive;

class SubscriptionBundle<T> implements Subscription {

    private final Executor observeOn;
    private final Observer<T> observer;
    private final ObservableDispose<T> dispose;

    public SubscriptionBundle(Executor observeOn, Observer<T> observer, ObservableDispose<T> dispose) {
        this.observeOn = observeOn;
        this.observer = observer;
        this.dispose = dispose;
    }

    public Executor getObserveOn() {
        return observeOn;
    }

    public Observer<T> getObserver() {
        return observer;
    }

    @Override
    public void dispose() {
        dispose.dispose(this);
    }
}
