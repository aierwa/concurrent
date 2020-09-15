package com.xuxiang.learn.multithread.tools.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 烧水泡茶
 * 用 FutureTask 获取线程执行的结果
 *
 * @author xuxiang
 * 2020/9/14
 */
public class MakeTea {

    static class EnjoyTea implements Callable<String> {
        private FutureTask<String> takeTea;

        public EnjoyTea(FutureTask<String> takeTea) {
            this.takeTea = takeTea;
        }

        @Override
        public String call() throws Exception {
            System.out.println("T1：洗水壶...");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("T1：烧开水...");
            TimeUnit.SECONDS.sleep(10);

            String teaName = takeTea.get();
            System.out.println("T1：拿到茶叶..." + teaName);
            System.out.println("T1：泡茶..." + teaName);
            return "上茶：" + teaName;
        }
    }

    static class TakeTea implements Callable<String> {

        @Override
        public String call() throws Exception {
            System.out.println("T2：洗茶壶...");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("T2：洗茶杯...");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("T2：拿茶叶...");
            TimeUnit.SECONDS.sleep(1);
            return "龙井";
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> takeTea = new FutureTask<>(new TakeTea());
        FutureTask<String> enjoyTea = new FutureTask<>(new EnjoyTea(takeTea));

        Thread t1 = new Thread(enjoyTea);
        Thread t2 = new Thread(takeTea);
        t1.start();
        t2.start();
        System.out.println(enjoyTea.get());
    }
}
