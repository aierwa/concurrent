package com.xuxiang.learn.multithread.tools.stampedlock;

import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock 示例
 * StampedLock 的悲观读和写意义和 ReadWriteLock 是一样的
 * 如果使用乐观读的话，性能还是很好的
 *
 * @author xuxiang
 * 2020/8/27
 */
public class Point {
    private double x, y;
    private final StampedLock stampedLock = new StampedLock();

    /**
     * 计算到原点的距离
     *
     * @return
     */
    public double distanceFromOrigin() {
        // 开始乐观读（实际上是不加锁的）
        long stamp = stampedLock.tryOptimisticRead();

        double curX = x, curY = y;
        // 在乐观读的时候可能有写操作，所以要判断一下
        if (!stampedLock.validate(stamp)) {
            // 如果发生写操作，需要重新读 xy，直接升级为悲观读锁
            stamp = stampedLock.readLock();
            try {
                curX = x;
                curY = y;
            } finally {
                // 释放悲观读锁
                stampedLock.unlockRead(stamp);
            }
        }

        // 计算过程中 xy 变化可以忍受，至少保证了 xy 数据是一致的
        return Math.sqrt(curX * curX + curY * curY);
    }

    /**
     * 如果在原点就移动到新点
     * 注意点：需要先读，读了后判断，如果在原点就需要继续写，所以涉及 读锁升级写锁 的过程
     *
     * @param newX
     * @param newY
     */
    public void moveIfAtOrigin(double newX, double newY) {
        // 先加读锁
        long stamp = stampedLock.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                // 判断到在原点，开始升级为写锁
                long ws = stampedLock.tryConvertToWriteLock(stamp);
                if (ws != 0) {
                    // 升级成功，ws 为写锁的戳，赋值给 stamp
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    // 升级失败，直接释放悲观读锁，然后去获取写锁
                    stampedLock.unlockRead(stamp);
                    stamp = stampedLock.writeLock();
                }
            }
        } finally {
            stampedLock.unlock(stamp);
        }

    }
}
