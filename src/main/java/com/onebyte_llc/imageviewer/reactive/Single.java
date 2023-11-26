/*
 *     ImageViewer - free image viewing gui
 *     Copyright (C) 2023  Sean Horton
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; version 2 of the License
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.onebyte_llc.imageviewer.reactive;

import java.util.ArrayList;
import java.util.List;

public class Single<T> {

    private final List<SubscriptionBundle<T>> subscriptions = new ArrayList<>();
    private final Executor executor = Executor.processThread();

    private Executor subscribeOn = Executor.processThread();
    private Emitter<T> onSubscribeEmitter;
    private T item;

    public Single<T> subscribeOn(Executor executor) {
        this.subscribeOn = executor;
        return this;
    }

    public Single<T> onSubscribe(Emitter<T> runnable) {
        this.onSubscribeEmitter = runnable;
        return this;
    }

    public Observable<T> observe() {
        return new Observable<>((subscription) -> {
            executor.run(() -> {
                if (subscriptions.size() == 0 && onSubscribeEmitter != null) {
                    onSubscribeEmitter.onSubscribe(Single.this);
                }
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
            item = data;
            notifyAllSubscribers();
        });
    }

    private void notifyAllSubscribers() {
        if (subscriptions.isEmpty() || item == null) {
            return;
        }

        subscriptions.forEach(sub -> {
            sub.getObserveOn().run(() -> sub.getObserver().notify(item));
        });
    }

    public interface Emitter<T> {
        void onSubscribe(Single<T> single);
    }

}
