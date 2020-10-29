package com.xuxiang.learn.multithread.designpattern.threadlocal;

import java.text.SimpleDateFormat;

/**
 * SimpleDateFormat 本来是线程不安全的
 * 使用 ThreadLocal 实现线程安全的 SimpleDateFormat
 * @author xuxiang
 * 2020/9/29
 */
public class SafeDateFormat {
    // 每个线程单独享有一个 sdf 对象
    private static final ThreadLocal<SimpleDateFormat> tl =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static SimpleDateFormat get() {
        return tl.get();
    }
}
