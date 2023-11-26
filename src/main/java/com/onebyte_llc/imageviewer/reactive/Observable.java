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
