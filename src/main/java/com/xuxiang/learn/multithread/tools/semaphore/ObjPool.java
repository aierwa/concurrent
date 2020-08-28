package com.xuxiang.learn.multithread.tools.semaphore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * 通过 semaphore 实现一个限流对象池
 * 可以允许多个线程同时进入临界区
 * 池化技术主要有线程池，内存池，连接池，对象池等等
 * 池化技术作用：节约大量的的系统资源，使得更多的CPU时间和内存用来处理实际的业务逻辑，而不是频繁的线程创建与销毁。
 *
 * @author xuxiang
 * 2020/8/27
 */
public class ObjPool<T, R> {
    private final List<T> pool;
    private final Semaphore semaphore;

    public ObjPool(List<T> initList) {
        // 初始化对象，CopyOnWriteArrayList or Vector
        pool = new CopyOnWriteArrayList<>(initList);
        // 最多允许 size 数量的线程进入临界区，某个线程用完后会释放一个入口出来
        this.semaphore = new Semaphore(initList.size());
    }

    public R exec(Function<T, R> func) throws Exception {
        // 申请进入临界区
        semaphore.acquire();
        T t = null;
        try {
            // 从第一位取一个对象（因为涉及多线程，所以这里 pool 需要为线程安全的 list）
            t = pool.remove(0);
            return func.apply(t);
        } finally {
            // 归还对象
            pool.add(t);
            // 释放入口
            semaphore.release();
        }
    }

    static class Connection {
        private String host;

        Connection(String host) {
            this.host = host;
        }

        String getHost() {
            return this.host;
        }
    }

    public static void main(String[] args) {
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            connections.add(new Connection("host-" + (i + 1)));
        }
        ObjPool<Connection, String> pool = new ObjPool<>(connections);
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                try {
                    System.out.println(pool.exec(Connection::getHost));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
