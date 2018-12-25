package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import com.vgw.demo.gameweb.message.actor.TableInfo;

public class TableActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef lobbyActor;
    private int tableId;

    // Props == Object creation hint
    static public Props props(TableInfo tableInfo, ActorRef lobbyActor) {
        return Props.create(TableActor.class, () -> new TableActor(tableInfo, lobbyActor));
    }

    public TableActor(TableInfo tableInfo, ActorRef lobbyActor){
        this.tableId=tableInfo.getTableId();
        this.lobbyActor=lobbyActor;
        log.info(String.format("Create Table:%d",tableInfo.getTableId()));
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ConnectInfo.class, c -> {
                })
                .match(Greeting.class, c -> {
                    String name = c.getName();
                    getSender().tell("Hello, " + name ,getSelf()); // response for test
                })
                .build();
    }

}

