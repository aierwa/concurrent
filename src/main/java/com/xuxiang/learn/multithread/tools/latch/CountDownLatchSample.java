package com.xuxiang.learn.multithread.tools.latch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xuxiang
 * 2020/8/28
 */
public class CountDownLatchSample {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 不会恢复
        CountDownLatch latch = new CountDownLatch(2);
        executorService.execute(() -> {
            System.out.println("t1 start...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t1 end...");
            latch.countDown();
        });
        executorService.execute(() -> {
            System.out.println("t2 start...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 end...");
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main end");

    }
}
