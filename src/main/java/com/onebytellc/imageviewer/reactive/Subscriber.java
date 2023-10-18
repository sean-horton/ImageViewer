package com.onebytellc.imageviewer.reactive;

import java.util.ArrayList;
import java.util.List;

public class Subscriber<T> {

    private final List<SubscriptionBundle<T>> subscriptions = new ArrayList<>();
    private final Executor executor = Executor.processThread();
    private final Runnable initialSubscribeListner;

    private T data;

    public Subscriber() {
        initialSubscribeListner = () -> {
        };
    }

    public Subscriber(Runnable onInitialSubscribe) {
        initialSubscribeListner = onInitialSubscribe;
    }

    public Observable<T> observe() {
        return new Observable<>((subscription) -> {
            executor.run(() -> {
                subscriptions.add(subscription);
                if (data != null) {
                    subscription.getObserver().notify(data);
                }
                if (subscriptions.size() == 1) {
                    initialSubscribeListner.run();
                }
            });
        }, (subscription) -> {
            executor.run(() -> {
                subscriptions.remove(subscription);
                if (subscriptions.size() == 0) {

                }
            });
        });
    }

    public void notify(T data) {
        executor.run(() -> {
            this.data = data;
            subscriptions.forEach(sub -> {
                sub.getObserveOn().run(() -> sub.getObserver().notify(data));
            });
        });
    }

}
