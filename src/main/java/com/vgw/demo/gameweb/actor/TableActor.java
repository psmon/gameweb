package com.vgw.demo.gameweb.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import com.vgw.demo.gameweb.message.actor.GameTick;
import com.vgw.demo.gameweb.message.actor.TableInfo;

import java.time.Duration;


public class TableActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef lobbyActor;
    private ActorRef gameActor;
    private Cancellable gameCancellable;

    private int tableId;

    private ActorSystem system;

    // Props == Object creation hint
    static public Props props(TableInfo tableInfo, ActorRef lobbyActor) {
        return Props.create(TableActor.class, () -> new TableActor(tableInfo, lobbyActor));
    }

    public TableActor(TableInfo tableInfo, ActorRef lobbyActor){
        this.tableId=tableInfo.getTableId();
        this.lobbyActor=lobbyActor;
        system = getContext().getSystem();
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
                .match(TableInfo.class, t->{
                    if(t.getCmd()== TableInfo.TableCmd.CREATE){
                        String gameUID = "game-" + tableId;
                        gameActor = getContext().actorOf( GameActor.props(t,getSelf()), gameUID);
                        log.info(String.format("Create Table:%d",t.getTableId()));

                        gameCancellable = system.scheduler().schedule(Duration.ZERO,
                                Duration.ofMillis(100), gameActor, new GameTick(),
                                system.dispatcher(), null);
                    }else if(t.getCmd() == TableInfo.TableCmd.DELETE){
                        //TODO: Just Cancel or Ask for Stop game
                        gameCancellable.cancel();
                    }
                })
                .build();
    }

}

