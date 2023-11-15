package com.onebyte_llc.imageviewer.reactive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Streamable<T> {

    private final List<SubscriptionBundle<T>> subscriptions = new ArrayList<>();
    private final Executor executor = Executor.processThread();

    private Queue<T> stream = new LinkedList<>();

    public Observable<T> observe() {
        return new Observable<>((subscription) -> {
            executor.run(() -> {
                subscriptions.add(subscription);
                notifyAllSubscribers();
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
            stream.add(data);
            notifyAllSubscribers();
        });
    }

    private void notifyAllSubscribers() {
        if (subscriptions.isEmpty()) {
            return;
        }

        while (!stream.isEmpty()) {
            T next = stream.poll();

            subscriptions.forEach(sub -> {
                sub.getObserveOn().run(() -> sub.getObserver().notify(next));
            });
        }
    }

}
