package com.xuxiang.learn.multithread.tools.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xuxiang
 */
public class LockSample {
    private int value = 0;
    private final Lock lock = new ReentrantLock();

    public void addOneNotSafe() {
        value++;
    }

    // 耗时 529 ms
    public synchronized void addOneSync() {
        value++;
    }

    // 耗时 698 ms
    public void addOneLock() {
        lock.lock();
        try {
            value++;
        } finally {
            lock.unlock();
        }
    }

    public int getValue() {
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        final LockSample lockSample = new LockSample();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000000; i++) {
                lockSample.addOneSync();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000000; i++) {
                lockSample.addOneSync();
            }
        });
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(lockSample.getValue());
        System.out.println("耗时 " + (System.currentTimeMillis() - start) + " ms");
    }
}
