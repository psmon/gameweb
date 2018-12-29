package com.vgw.demo.gameweb.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.actor.MessageWS;
import com.vgw.demo.gameweb.message.actor.PlayerList;
import org.springframework.messaging.handler.annotation.Payload;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

public class GameSend {

    protected ActorSystem system;
    private ActorRef lobbyActor;
    private ActorRef tableActor;

    GameSend(ActorSystem system, ActorRef lobbyActor, ActorRef tableActor) {
        this.system = system;
        this.lobbyActor = lobbyActor;
        this.tableActor = tableActor;
    }

    protected Object askToTable(Object askObj) throws Exception {
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> future = ask(tableActor, askObj, timeout);
        return Await.result(future, timeout.duration());
    }

    protected  void sendAll(@Payload GameMessage gameMessage) throws Exception {
        List<Player> viewList = (List<Player>) askToTable(new PlayerList(PlayerList.Cmd.ALL));
        for(Player ply:viewList){
            send(ply,gameMessage);
        }
    }

    protected void sendAll(@Payload GameMessage gameMessage,int delayMills) throws Exception {
        List<Player> viewList = (List<Player>) askToTable(new PlayerList(PlayerList.Cmd.ALL));
        for(Player ply:viewList){
            send(ply,gameMessage,delayMills);
        }
    }

    protected void send(Player player,@Payload GameMessage gameMessage){
        lobbyActor.tell(new MessageWS(player.getSession(),gameMessage), ActorRef.noSender());
    }

    protected void send(Player player,@Payload GameMessage gameMessage,int delayMills){
        system.scheduler().scheduleOnce( Duration.ofMillis(delayMills),
                lobbyActor ,new MessageWS(player.getSession(),gameMessage), system.dispatcher(), ActorRef.noSender() );
    }
}
