package com.xuxiang.learn.multithread.tools.barrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 两个线程都 await() 才继续做下个动作
 * 即：两个线程步调一致
 *
 * @author xuxiang
 * 2020/8/28
 */
public class CyclicBarrierSample {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        Thread t1 = new Thread(() -> {
            while (true) {
                System.out.println("t1 start...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1 end");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            while (true) {
                System.out.println("t2 start...");
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 end");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println("t2 end end");
            }
        });
        t1.start();
        t2.start();
    }
}
