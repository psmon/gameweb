package com.vgw.demo.gameweb.controler;

import com.vgw.demo.gameweb.fakegame.Lobby;
import com.vgw.demo.gameweb.fakegame.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.SessionMessage;
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

        String  sessionId = headerAccessor.getUser().getName();
        logger.info("GameMsg:" + gameMessage );
        String gamePacket = gameMessage.getContent();
        String splitMessage[] = gamePacket.split("!!");

        String userName = headerAccessor.getSessionAttributes().get("username").toString();
        String userSession =  headerAccessor.getUser().getName();

        Object objTableNo = headerAccessor.getSessionAttributes().get("tableNo");
        Integer tableNo = objTableNo!=null? (Integer)objTableNo : -1;

        if(gameMessage.getType()== GameMessage.MessageType.GAME){
            switch (gameMessage.getContent()){
                case "join":{
                    int ftableNo = gameMessage.getNum1();
                    lobby.joinGameTable(ftableNo,userName,sessionId);
                    headerAccessor.getSessionAttributes().put("tableNo",Integer.valueOf(ftableNo));
                }
                break;
                case "seat":{
                    Player player = new Player();
                    player.setName(userName);
                    player.setSession(userSession);
                    //Todo : make UserRepository
                    player.setTotalMoney(1000);
                    player.setChips(1000);
                    lobby.getTable(tableNo).seatUser(player);
                }
                break;

            }
            return null;
        }
        else if(gameMessage.getType()== GameMessage.MessageType.ACTION){
            SessionMessage sessionMessage = new SessionMessage();
            sessionMessage.session=userSession;
            sessionMessage.name=userName;
            sessionMessage.gameMessage=gameMessage;
            lobby.getTable(tableNo).getGame().addGameMessage(sessionMessage);
            return null;
        }

        return gameMessage;
    }
}
