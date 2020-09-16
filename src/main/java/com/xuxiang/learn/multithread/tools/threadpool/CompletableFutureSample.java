package com.xuxiang.learn.multithread.tools.threadpool;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author xuxiang
 * 2020/9/15
 */
public class CompletableFutureSample {
    public static void main(String[] args) {
        // 烧水线程
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            System.out.println("T1:洗水壶...");
            sleep(1);
            System.out.println("T1:烧开水...");
            sleep(15);
        });
        // 拿茶叶线程
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2:洗茶壶");
            sleep(2);
            System.out.println("T2:洗茶杯");
            sleep(2);
            System.out.println("T2:拿茶叶");
            sleep(2);
            return "龙井";
        });

        // 泡茶线程，等待烧水成功，以及拿茶叶成功
        CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf) -> {
            System.out.println("T3: 拿到茶叶：" + tf);
            System.out.println("T3: 泡茶...");
            sleep(1);
            return "上茶：" + tf;
        });

        System.out.println(f3.join());
    }

    private static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
