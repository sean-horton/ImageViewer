package com.onebyte_llc.imageviewer.reactive;

public class Observable<T> {

    private final ObservableSubscribe<T> subscribeListener;
    private final ObservableDispose<T> disposeListener;

    private Executor subscribeOn = Executor.processThread();
    private Executor observeOn = Executor.processThread();

    Observable(ObservableSubscribe<T> listener, ObservableDispose<T> disposeListener) {
        this.subscribeListener = listener;
        this.disposeListener = disposeListener;
    }

    public Observable<T> observeOn(Executor executor) {
        this.observeOn = executor;
        return this;
    }

    public Subscription subscribe(Observer<T> observer) {
        SubscriptionBundle<T> bundle = new SubscriptionBundle<>(observeOn, observer, disposeListener);
        subscribeOn.run(() -> subscribeListener.onSubscribe(bundle));
        return bundle;
    }

}
