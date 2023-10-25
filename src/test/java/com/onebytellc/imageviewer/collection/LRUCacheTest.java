package com.onebytellc.imageviewer.collection;

import com.onebytellc.imageviewer.collections.LRUCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LRUCacheTest {

    @Test
    public void simple() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(0, "0");
        cache.put(1, "1");
        cache.put(2, "2");

        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("0", cache.get(0));
            Assertions.assertEquals("2", cache.get(2));
            Assertions.assertEquals("1", cache.get(1));
        }
    }

    @Test
    public void checkLRU() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(0, "0");
        Assertions.assertEquals("0", cache.get(0));

        cache.put(1, "1");
        Assertions.assertEquals("0", cache.get(0));
        Assertions.assertEquals("1", cache.get(1));

        cache.put(2, "2");
        Assertions.assertEquals("1", cache.get(1));
        Assertions.assertEquals("0", cache.get(0));
        Assertions.assertEquals("2", cache.get(2));

        cache.put(3, "3");
        Assertions.assertNull(cache.get(1));
        Assertions.assertEquals("3", cache.get(3));
        Assertions.assertEquals("0", cache.get(0));
        Assertions.assertEquals("2", cache.get(2));

        cache.put(4, "4");
        Assertions.assertNull(cache.get(3));
        Assertions.assertEquals("2", cache.get(2));
        Assertions.assertEquals("4", cache.get(4));
        Assertions.assertEquals("0", cache.get(0));
    }

}
