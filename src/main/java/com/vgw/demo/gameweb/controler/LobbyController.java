package com.vgw.demo.gameweb.controler;

import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.thread.Lobby;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    @Autowired
    private Lobby lobby;


    @MessageMapping("/lobby.addUser")
    @SendTo("/topic/public")
    public GameMessage addUser(@Payload GameMessage gameMessage,
                               SimpMessageHeaderAccessor headerAccessor) {

        String  sessionId = headerAccessor.getSessionId();
        logger.info("Add User:" + sessionId );
        gameMessage.setContent("added user..");
        //String secuname = headerAccessor.setUser();
        // Add username in web socket session
        try{
            headerAccessor.getSessionAttributes().put("username", gameMessage.getSender());
            headerAccessor.getSessionAttributes().put("ws-session", headerAccessor.getUser().getName());
        }catch (Exception e){
            gameMessage.setContent("auth error");
        }
        return gameMessage;
    }

}
