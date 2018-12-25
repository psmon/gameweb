package com.vgw.demo.gameweb.controler;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.vgw.demo.gameweb.fakegame.Lobby;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    // Todo : Replacing the Object approach with a message approach.
    // Lobby(Object) ==> ActorSystem...

    @Autowired
    Lobby lobby;

    @Autowired
    private ActorSystem system;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getUser().getName();
    }

    @EventListener
    void handleSessionConnectedEvent(SessionConnectedEvent event) {
        // Get Accessor
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getUser().getName();

        // OOP VS MESSAGE

        // #OOP - It's simple to develop locally, but many things need to change in order to be extended remotely.
        lobby.addSender(sessionId,messagingTemplate);

        // #ACTOR - This can be extended to remote without implementation changes.
        ActorSelection lobby = system.actorSelection("/user/lobby");
        lobby.tell(new ConnectInfo(sessionId,messagingTemplate, ConnectInfo.ConnectCmd.CONNECT), ActorRef.noSender());

        // Suppose you create several child actors under table.
        // You can send a message to all children with the following commands.
        // This is very useful.
        // Sameple Cmd : ActorSelection lobby = system.actorSelection("/user/table/*");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String session = headerAccessor.getUser().getName();

        if(username != null) {
            logger.info("User Disconnected : " + username);
            GameMessage gameMessage = new GameMessage();
            gameMessage.setType(GameMessage.MessageType.LEAVE);
            gameMessage.setSender(username);
            //messagingTemplate.convertAndSend("/topic/public", gameMessage);

            // #OOP
            lobby.removeSender(session);

            // #ACTOR
            ActorSelection lobby = system.actorSelection("/user/lobby");
            lobby.tell(new ConnectInfo(session,messagingTemplate, ConnectInfo.ConnectCmd.DISCONET), ActorRef.noSender());
        }
    }

}
