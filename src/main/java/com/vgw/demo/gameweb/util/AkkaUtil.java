package com.vgw.demo.gameweb.util;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

public class AkkaUtil {

    final static public int TIMEOUT_ACTOR_SELECT = 1;
    static public Map<String,ActorRef> ActorStore = new HashMap<>();

    // Sync Utils
    //
    static public Object AskToActor(ActorRef targetActor, Object askObj,int timeOut) throws Exception {
        FiniteDuration duration = FiniteDuration.create(timeOut, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> future = ask(targetActor, askObj, timeout);
        return Await.result(future, timeout.duration());
    }

    // Diffrent between ActorRef and ActorSelection : https://doc.akka.io/docs/akka/snapshot/general/addressing.html
    static public ActorRef SelectToRef(ActorSelection actorSel) throws Exception {
        FiniteDuration duration = FiniteDuration.create(TIMEOUT_ACTOR_SELECT, TimeUnit.SECONDS);
        Future<ActorRef> fut = actorSel.resolveOne(duration);
        return Await.result(fut, duration);
    }

    static public Object AskToActorSelect(ActorSelection actorSel, Object askObj,int timeOut) throws Exception {
        ActorRef actorRef = SelectToRef(actorSel);
        return AskToActor(actorRef,askObj,timeOut);
    }

    // ASync Utils

}
