package com.xuxiang.learn.multithread.other;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 使用 actor 实现并发累加器
 *
 * @author xuxiang
 */
public class CounterActor extends UntypedAbstractActor {
    private int count = 0;

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Number) {
            count += ((Number) message).intValue();
            System.out.println("add " + message + ", count now is " + count);
        } else {
            System.out.println("no add, count now is " + count);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建 actor 系统
        ActorSystem system = ActorSystem.create("helloActor");
        ActorRef counterActor = system.actorOf(Props.create(CounterActor.class));

        // 4个线程生产消息
        ExecutorService es = Executors.newFixedThreadPool(4);

        // 生产 4*100000 个消息
        for (int i = 0; i < 4; i++) {
            es.execute(() -> {
                for (int i1 = 0; i1 < 100000; i1++) {
                    counterActor.tell(1, ActorRef.noSender());
                }
            });
        }

        // 关闭线程池
        es.shutdown();
        counterActor.tell("", ActorRef.noSender());
        Thread.sleep(10000);
        counterActor.tell("", ActorRef.noSender());
        // 关闭 actor 系统
        system.terminate();
    }
}
