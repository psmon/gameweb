package com.vgw.demo.gameweb.controler;

import com.vgw.demo.gameweb.fakegame.Lobby;
import com.vgw.demo.gameweb.message.GameMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @Autowired
    Lobby lobby;

    @MessageMapping("/game.joingame")
    @SendTo("/topic/public")
    public GameMessage addUser(@Payload GameMessage gameMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", gameMessage.getSender());
        gameMessage.setContent("added Succed..");
        return gameMessage;
    }
}
