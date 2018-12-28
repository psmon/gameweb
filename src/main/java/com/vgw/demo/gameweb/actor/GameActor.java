package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.fakegame.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.GameTick;
import com.vgw.demo.gameweb.message.actor.JoinPlyReq;
import com.vgw.demo.gameweb.message.actor.TableCreateReq;
import org.springframework.messaging.handler.annotation.Payload;

public class GameActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef tableActor;
    private int gameid;
    private int tickCnt;

    static public Props props(TableCreateReq tableCreateReq, ActorRef tableActor) {
        return Props.create(GameActor.class, () -> new GameActor(tableCreateReq, tableActor));
    }

    public GameActor(TableCreateReq tableCreateReq, ActorRef tableActor){
        this.gameid= tableCreateReq.getTableId();
        this.tableActor=tableActor;
        tickCnt=0;
        log.info(String.format("Create Game:%d", tableCreateReq.getTableId()));
    }

    protected void connectPly(Player ply){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("readytable");
        gameMessage.setNum1(gameid);
        send(ply,gameMessage);
        /*
        for(Player other:table.getPlayList(false)){
            sendSeatInfo(other,false,ply);
        }*/
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
                .match(JoinPlyReq.class, j->{
                    connectPly(j.getPly());
                })
                .build();
    }

    protected void sendAll(@Payload GameMessage gameMessage){
        /*
        for(Player ply:table.viewList){
            send(ply,gameMessage);
        }*/
    }

    protected void send(Player player,@Payload GameMessage gameMessage){
        /*
        SimpMessageSendingOperations messagingTemplate = Lobby.getSender(player.getSession());
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(player.getSession());
        headerAccessor.setLeaveMutable(true);
        GameMessage gameMessage2 = new GameMessage();
        gameMessage2.setType(GameMessage.MessageType.GAME);
        messagingTemplate.convertAndSendToUser(player.getSession(),"/topic/public",gameMessage );*/
    }


}
