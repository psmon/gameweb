package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.actor.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

// LobbyActor + SocketHandler
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("Duplicates")
public class LobbyActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<String, SimpMessageSendingOperations> sessionMgr = new HashMap<>();

    // Props == Object creation hint
    static public Props props() {
        return Props.create(LobbyActor.class, () -> new LobbyActor());
    }

    private void joinGameTable(int tableId,String name,String session) throws Exception {
        Player ply = new Player();
        ply.setName(name);
        ply.setSession(session);
        findTableByID(tableId).tell(new JoinPly(ply),getSender());
    }

    private ActorRef findTableByID(int tableID) throws Exception {
        String tableActorPath = "/user/lobby/table-"+tableID;
        ActorSelection tableSelect = this.getContext().actorSelection(tableActorPath);
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Future<ActorRef> fut = tableSelect.resolveOne(duration);
        ActorRef tableActor = Await.result(fut, duration);
        return tableActor;
    }


    private ActorRef findTableALL() throws Exception {
        String tableActorPath = "/*";
        ActorSelection tableSelect = this.getContext().actorSelection(tableActorPath);
        FiniteDuration duration = FiniteDuration.create(3, TimeUnit.SECONDS);
        Future<ActorRef> fut = tableSelect.resolveOne(duration);
        ActorRef tableActor = Await.result(fut, duration);
        return tableActor;
    }



    protected void send(String sessionId,@Payload GameMessage gameMessage){
        SimpMessageSendingOperations messagingTemplate = sessionMgr.get(sessionId);
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        GameMessage gameMessage2 = new GameMessage();
        gameMessage2.setType(GameMessage.MessageType.GAME);
        messagingTemplate.convertAndSendToUser(sessionId,"/topic/public",gameMessage );
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ConnectInfo.class, c -> {
                    if(c.getCmd()== ConnectInfo.Cmd.CONNECT){
                        sessionMgr.put(c.getSessionId(),c.getWsSender());
                        log.info("user connected:"+c.getSessionId());
                        getSender().tell("done",ActorRef.noSender());
                    }else if(c.getCmd()== ConnectInfo.Cmd.DISCONET){
                        sessionMgr.remove(c.getSessionId());
                        Player removeUser = new Player();
                        removeUser.setSession(c.getSessionId());

                        if(c.getTableNo()>0){
                            findTableByID(c.getTableNo()).tell(new SeatOut(removeUser),ActorRef.noSender());
                        }else{
                            findTableALL().tell(new SeatOut(removeUser),ActorRef.noSender());
                        }
                        log.info("user disconnected:"+c.getSessionId());
                        getSender().tell("done",ActorRef.noSender());
                    }else if(c.getCmd() == ConnectInfo.Cmd.FIND){
                        //Just For Test
                        if(sessionMgr.containsKey(c.getSessionId())){
                            getSender().tell("User exists",ActorRef.noSender());
                        }else{
                            getSender().tell("User does not exist",ActorRef.noSender());
                        }
                    }
                })
                .match(TableCreate.class, t->{
                    // Create a table under the lobby, if you have an Actor named TableManagement, you can move easily.
                    String tableUID = "table-" + t.getTableId();
                    if(t.getCmd() == TableCreate.Cmd.CREATE){
                        ActorRef tableActor = getContext().actorOf( TableActor.props(t,this.getSelf() ), tableUID);
                        tableActor.tell(t,ActorRef.noSender());
                        getSender().tell("created",ActorRef.noSender());
                    }
                })
                .match(JoinGame.class, j->{
                    joinGameTable(j.getTableId(),j.getName(),j.getSession());
                })
                .match(MessageWS.class, m->{
                    send(m.getSession(),m.getGameMessage());
                })
                .build();
    }
}
