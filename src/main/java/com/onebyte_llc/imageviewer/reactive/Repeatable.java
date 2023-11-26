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

public class Repeatable<T> {

    private final List<SubscriptionBundle<T>> subscriptions = new ArrayList<>();
    private final Executor executor = Executor.processThread();
    private final Runnable initialSubscribeListner;

    private T data;

    public Repeatable() {
        initialSubscribeListner = () -> {
        };
    }

    public Repeatable(Runnable onInitialSubscribe) {
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
