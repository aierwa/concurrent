package com.xuxiang.learn.multithread.tools.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 线程池的简单原理示例
 * 生产者 - 消费者模式
 *
 * @author xuxiang
 * 2020/9/14
 */
public class MyThreadPool {
    private BlockingQueue<Runnable> queue;
    private List<WorkThread> threads = new ArrayList<>();

    public MyThreadPool(int poolSize, BlockingQueue<Runnable> queue) {
        this.queue = queue;
        for (int i = 0; i < poolSize; i++) {
            WorkThread thread = new WorkThread();
            thread.start();
            threads.add(thread);
        }
    }

    void execute(Runnable runnable) {
        try {
            queue.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class WorkThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    queue.take().run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
