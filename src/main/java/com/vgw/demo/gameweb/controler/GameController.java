package com.vgw.demo.gameweb.controler;

import com.vgw.demo.gameweb.fakegame.Lobby;
import com.vgw.demo.gameweb.message.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);


    @Autowired
    Lobby lobby;

    @MessageMapping("/game.req")
    @SendTo("/topic/public")
    public GameMessage gameReq(@Payload GameMessage gameMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        //headerAccessor.getSessionAttributes().put("username", gameMessage.getSender());
        //gameMessage.setContent("added Succed..");

        String  sessionId = headerAccessor.getUser().getName();

        logger.info("GameMsg:" + gameMessage );

        String gamePacket = gameMessage.getContent();
        String splitMessage[] = gamePacket.split("!!");

        if(splitMessage!=null && splitMessage.length>1){
            String messageName = splitMessage[0];
            switch (messageName){
                case "join":{
                    int tableNo = Integer.parseInt(splitMessage[1]);
                    String userName = headerAccessor.getSessionAttributes().get("username").toString();
                    lobby.joinGameTable(tableNo,userName,sessionId);
                }
                break;
            }
            GameMessage expectMsg=new GameMessage();
            expectMsg.setContent("wait for some msg");
            expectMsg.setType(GameMessage.MessageType.NONE);
            return null;
        }
        return gameMessage;
    }


}
