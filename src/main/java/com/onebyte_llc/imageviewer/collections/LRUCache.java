package com.onebyte_llc.imageviewer.collections;

import java.util.HashMap;
import java.util.Map;

/**
 * Basically a Guava cache, but I wanted to write my own
 */
public class LRUCache<K, V> {

    private final int limit;
    private final Map<K, Container<K, V>> map = new HashMap<>();
    private final ListNode<K> sentinal;
    private ListNode<K> tail;

    public LRUCache(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        this.limit = limit;
        this.sentinal = new ListNode<>(null);
        this.tail = sentinal;
    }

    public synchronized V get(K key) {
        Container<K, V> container = map.get(key);
        if (container == null) {
            return null;
        }

        markAsMostRecent(container);
        return container.value;
    }

    public synchronized void put(K key, V value) {
        if (map.size() == limit) {
            ListNode<K> toRemove = sentinal.next;
            ListNode<K> next = sentinal.next.next;
            next.prev = sentinal;
            sentinal.next = next;

            map.remove(toRemove.key);
        }

        map.compute(key, (k, vContainer) -> {
            if (vContainer == null) {
                ListNode<K> next = new ListNode<>(k);
                vContainer = new Container<>(next, value);
                tail.next = next;
                next.prev = tail;
                tail = next;
                return vContainer;
            } else {
                vContainer.value = value;
                markAsMostRecent(vContainer);
                return vContainer;
            }
        });
    }

    public synchronized boolean contains(K key) {
        return map.containsKey(key);
    }

    private void markAsMostRecent(Container<K, V> container) {
        if (tail == container.node) {
            return;
        }

        // move this container to end of LRU list
        ListNode<K> newEnd = container.node;

        ListNode<K> prev = newEnd.prev;
        ListNode<K> next = newEnd.next;

        prev.next = next;
        next.prev = prev;

        tail.next = newEnd;
        newEnd.prev = tail;
        newEnd.next = null;
        tail = newEnd;
    }

    private static class Container<K, V> {
        final ListNode<K> node;
        V value;

        public Container(ListNode<K> node, V value) {
            this.node = node;
            this.value = value;
        }
    }

    private static class ListNode<K> {
        ListNode<K> prev;
        ListNode<K> next;
        final K key;

        public ListNode(K key) {
            this.key = key;
        }
    }

}
