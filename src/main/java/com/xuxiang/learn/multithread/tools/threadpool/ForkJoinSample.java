package com.xuxiang.learn.multithread.tools.threadpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 任务拆分、任务分治
 * @author xuxiang
 * 2020/9/22
 */
public class ForkJoinSample {
    public static void main(String[] args) {
        // 线程池大小为 CPU 核数，这样可以跑满 CPU 性能
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
//        // 创建分治任务
//        Fibonacci fibonacci = new Fibonacci(30);
//        // 启动分治任务
//        long start = System.currentTimeMillis();
//        Integer sum = forkJoinPool.invoke(fibonacci);
//        System.out.println("fibonacci sum : " + sum);
//        System.out.println("耗时 : " + (System.currentTimeMillis() - start));

        String[] inputs = new String[]{
            "hello world",
            "hello java",
            "java is good",
            "my name is xuxiang"
        };
        // 创建分治任务
        WordCount wordCount = new WordCount(inputs, 0, inputs.length);
        // 启动分治任务
        long start = System.currentTimeMillis();
        Map<String, Long> result = forkJoinPool.invoke(wordCount);
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            System.out.println(String.format("count for %s is %d", entry.getKey(), entry.getValue()));
        }
        System.out.println("耗时 : " + (System.currentTimeMillis() - start));
    }


    /**
     * 斐波那契数列（求数列第 n 个元素）
     */
    static class Fibonacci extends RecursiveTask<Integer> {
        private int n;

        public Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return n;
            }
            // 进行任务拆分
            Fibonacci f1 = new Fibonacci(n - 1);
            // 异步执行 f1
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            // 之所以用 f2.compute，是让当前线程有任务可做
            // 递归：当前项 = 前两项之和，直到小于 1 则为收敛为 1。
            return f2.compute() + f1.join();
        }
    }


    static class WordCount extends RecursiveTask<Map<String, Long>> {
        private String[] inputs;
        private int start, end;

        public WordCount(String[] inputs, int start, int end) {
            this.inputs = inputs;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Map<String, Long> compute() {
            if (end - start == 1) {
                return calc(inputs[start]);
            } else {
                // 如果没有具体到单一行，那么就拆分任务
                int mid = (start + end) / 2;
                WordCount wc1 = new WordCount(inputs, start, mid);
                WordCount wc2 = new WordCount(inputs, mid, end);
                wc1.invoke();
                return merge(wc2.compute(), wc1.join());
            }
        }

        /**
         * 计算单行的单词
         * @param line
         * @return
         */
        private Map<String, Long> calc(String line) {
            Map<String, Long> map = new HashMap<>();
            String[] words = line.split("\\s+");
            for (String word : words) {
                map.put(word, map.get(word) == null ? 1 : map.get(word) + 1);
            }
            return map;
        }

        /**
         * 合并 m2 到 m1
         * @param m1
         * @param m2
         */
        private Map<String, Long> merge(Map<String, Long> m1, Map<String, Long> m2) {
            for (String s : m2.keySet()) {
                m1.put(s, m1.get(s) == null ? m2.get(s) : m1.get(s) + m2.get(s));
            }
            return m1;
        }
    }


}
