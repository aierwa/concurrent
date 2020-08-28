package com.xuxiang.learn.multithread.tools.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程可通过 condition 进行通信，即 等待-通知 机制
 * synchronized 也有，只是只有一个条件，而 lock 支持多个条件
 * 可以通过此机制实现 同步转异步，如 dubbo 的服务调用
 *
 * @author xuxiang
 * 2020/8/27
 */
public class ConditionSample {
    private final Lock lock = new ReentrantLock();
    private final Condition done = lock.newCondition();
    private int finished = 0;
    private static final long DEFAULT_TIMEOUT = 10 * 1000L;

    /**
     * 异步方法，不会立即返回调用结果
     */
    public void methodAsync() {
        System.out.println("method async, will return a temp result immediately.");
    }

    /**
     * 异步 api 转同步 api
     *
     * @return
     * @throws Exception
     */
    public int methodSync() throws Exception {
        System.out.println("transfer method async to sync");
        long start = System.currentTimeMillis();
        methodAsync();
        lock.lock();
        try {
            while (!isFinished()) {
                // 执行这句后，锁已经释放了
                done.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
                if (isFinished() || (System.currentTimeMillis() - start) > DEFAULT_TIMEOUT) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        if (!isFinished()) {
            throw new TimeoutException();
        }
        return finished;
    }

    public boolean isFinished() {
        return this.finished == 1;
    }

    /**
     * 另外一个线程调用此方法，通知等待线程结果已返回
     */
    public void doFinished() {
        lock.lock();
        try {
            this.finished = 1;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionSample conditionSample = new ConditionSample();
        Thread t1 = new Thread(() -> {
            System.out.println("t1 线程开始调用同步方法");
            try {
                long start = System.currentTimeMillis();
                int re = conditionSample.methodSync();
                System.out.println("结果=" + re + ",耗时 " + (System.currentTimeMillis() - start) + " ms");
            } catch (Exception e) {
                System.err.println(e);
            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println("t2 线程等待一段时间后调用 doFinished 方法");
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            conditionSample.doFinished();
        });
        t1.start();
        t2.start();
    }


}
