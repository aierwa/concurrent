package com.xuxiang.learn.multithread.tools.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 * 适合场景：读多写少
 *
 * @author xuxiang
 * 2020/8/27
 */
public class RwlSample {
    private Map<String, Integer> cache;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rl = lock.readLock();
    private final Lock wl = lock.writeLock();

    public RwlSample() {
        this.cache = new HashMap<>();
    }

    /**
     * 读，加读锁
     *
     * @param key
     * @return
     */
    public Integer get(String key) {
        rl.lock();
        try {
            return cache.get(key);
        } finally {
            rl.unlock();
        }
    }

    /**
     * 写，加写锁
     *
     * @param key
     * @param value
     */
    public void put(String key, Integer value) {
        wl.lock();
        try {
            cache.put(key, value);
        } finally {
            wl.unlock();
        }
    }
}
