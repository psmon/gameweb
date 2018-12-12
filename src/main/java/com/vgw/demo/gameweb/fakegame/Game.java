package com.vgw.demo.gameweb.fakegame;

import com.vgw.demo.gameweb.message.GameMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;

import java.util.ArrayList;
import java.util.List;

public class Game {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public enum GameState
    {
        WAIT,READY, START, RESULT
    };

    private Table table;

    private List<Player> player = new ArrayList<>();

    public Game(Table table){
    }

    protected void sendToAll(){

    }

    protected void send(Player player,@Payload GameMessage gameMessage){
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(player.getSession());
        headerAccessor.setLeaveMutable(true);

        messagingTemplate.convertAndSendToUser(player.getSession(),"/queue/something", gameMessage,
                headerAccessor.getMessageHeaders());
    }
}
