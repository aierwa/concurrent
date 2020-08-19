package com.xuxiang.learn.multithread.theory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞队列
 * 入队和出队都是阻塞操作
 *
 * @author xuxiang
 */
public class MyBlockingQueue<T> {
    // 使用链表存储
    List<T> elements = new ArrayList<>();
    // 锁
    final Lock lock = new ReentrantLock();
    // 条件变量：队列不空
    final Condition notEmpty = lock.newCondition();
    // 条件变量：队列不满
    final Condition notFull = lock.newCondition();

    final int size;

    public MyBlockingQueue(int size) {
        this.size = size;
    }

    /**
     * 入队：队满时阻塞
     *
     * @param x element
     */
    public void enq(T x) throws InterruptedException {
        lock.lock();
        try {
            while (elements.size() == this.size) {
                // 如果队列满，则阻塞等待，直到被通知队列不满
                // await 会释放锁
                notFull.await();
            }
            // 入队
            elements.add(x);
            // 每次入队后通知 notEmpty 条件，使用 signal 通知任一线程即可
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队：队空时阻塞
     */
    public void deq() throws InterruptedException {
        lock.lock();
        try {
            while (elements.isEmpty()) {
                notEmpty.await();
            }
            elements.remove(0);
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public int length() {
        return elements.size();
    }


    public static void main(String[] args) {
        MyBlockingQueue<Integer> myBlockingQueue = new MyBlockingQueue<>(100);

        // 150 个线程入队
        for (int i = 0; i < 150; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    myBlockingQueue.enq(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // 60 个线程出队
        for (int i = 0; i < 60; i++) {
            new Thread(() -> {
                try {
                    myBlockingQueue.deq();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finally queue length: " + myBlockingQueue.length());

    }

}
