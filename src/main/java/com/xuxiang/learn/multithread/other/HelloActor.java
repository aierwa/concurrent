package com.xuxiang.learn.multithread.other;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

/**
 * actor 模型
 * @author xuxiang
 */
public class HelloActor extends UntypedAbstractActor {
    public void onReceive(Object message) throws Throwable {
        System.out.println("hello " + message);
    }

    public static void main(String[] args) {
        // 创建 actor 系统
        ActorSystem system = ActorSystem.create("helloActor");
        // 创建 HelloActor
        ActorRef helloActor = system.actorOf(Props.create(HelloActor.class));
        // 发消息给 actor
        helloActor.tell("actor", ActorRef.noSender());
        System.out.println("end tag");
    }
}


