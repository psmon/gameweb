package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.GameTick;
import com.vgw.demo.gameweb.message.actor.TableInfo;

public class GameActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef tableActor;
    private int gameid;
    private int tickCnt;

    static public Props props(TableInfo tableInfo, ActorRef tableActor) {
        return Props.create(GameActor.class, () -> new GameActor(tableInfo, tableActor));
    }

    public GameActor(TableInfo tableInfo, ActorRef tableActor){
        this.gameid=tableInfo.getTableId();
        this.tableActor=tableActor;
        tickCnt=0;
        log.info(String.format("Create Game:%d",tableInfo.getTableId()));
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(GameTick.class, c -> {
                    if( (tickCnt%100)==0)
                        log.info(String.format("Game Tick:%d",gameid));

                    tickCnt++;
                    if(tickCnt>Integer.MAX_VALUE-1000){
                        tickCnt=0;
                        log.debug("Tick Reset");
                    }

                })
                .match(Greeting.class, c -> {
                    String name = c.getName();
                    getSender().tell("Hello, " + name ,getSelf()); // response for test
                })
                .build();
    }


}
