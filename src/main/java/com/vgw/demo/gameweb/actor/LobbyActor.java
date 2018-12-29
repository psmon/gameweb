package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.fakegame.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.actor.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// LobbyActor + SocketHandler
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("Duplicates")
public class LobbyActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<String, SimpMessageSendingOperations> sessionMgr = new HashMap<>();

    private void joinGameTable(int tableId,String name,String session){
        Player ply = new Player();
        ply.setName(name);
        ply.setSession(session);
        findTableByID(tableId).tell(new JoinPly(ply),ActorRef.noSender());
    }

    private ActorRef findTableByID(int tableID){
        return getContext().findChild("table-"+tableID).get();
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
                    }else if(c.getCmd()== ConnectInfo.Cmd.DISCONET){
                        sessionMgr.remove(c.getSessionId());
                        log.info("user disconnected:"+c.getSessionId());
                    }
                    sessionMgr.put(c.getSessionId(),c.getWsSender());
                })
                .match(TableCreate.class, t->{
                    // Create a table under the lobby, if you have an Actor named TableManagement, you can move easily.
                    String tableUID = "table-" + t.getTableId();
                    if(t.getCmd() == TableCreate.Cmd.CREATE){
                        ActorRef tableActor = getContext().actorOf( TableActor.props(t,this.getSelf() ), tableUID);
                        tableActor.tell(t,ActorRef.noSender());
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
