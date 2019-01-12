package com.vgw.demo.gameweb.controler.ws;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.SessionMessage;
import com.vgw.demo.gameweb.message.actor.ActionMessage;
import com.vgw.demo.gameweb.message.actor.JoinGame;
import com.vgw.demo.gameweb.message.actor.SeatIn;
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
@SuppressWarnings("Duplicates")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    Lobby lobby;

    @Autowired
    private ActorSystem system;

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
        int tableNo = objTableNo!=null? (int)objTableNo : -1;

        String tableActorPath = String.format("/user/lobby/table-%d",tableNo);
        String gameActorPath = String.format("/user/lobby/table-%d/game-%d",tableNo,tableNo);

        Boolean isActorMode = true;
        // THREAD VS ACTOR : http://wiki.webnori.com/display/AKKA/Actor

        if(isActorMode==false){
            // OOP
            if( gameMessage.getType()== GameMessage.MessageType.GAME){
                switch (gameMessage.getContent()){
                    case "join":{
                        int ftableNo = gameMessage.getNum1();
                        lobby.joinGameTable(ftableNo,userName,sessionId);
                        headerAccessor.getSessionAttributes().put("tableNo",ftableNo);
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

        }else{
            //ACTOR
            if(gameMessage.getType()== GameMessage.MessageType.GAME){
                switch (gameMessage.getContent()){
                    case "join":{
                        int ftableNo = gameMessage.getNum1();
                        headerAccessor.getSessionAttributes().put("tableNo",ftableNo);
                        ActorSelection lobbyActor = system.actorSelection("user/lobby");
                        lobbyActor.tell(new JoinGame(ftableNo,userName,userSession),ActorRef.noSender() );
                    }
                    break;
                    case "seat":{
                        Player player = new Player();
                        player.setName(userName);
                        player.setSession(userSession);
                        //Todo : make UserRepository
                        player.setTotalMoney(1000);
                        player.setChips(1000);
                        ActorSelection tableActor = system.actorSelection(tableActorPath);
                        tableActor.tell(new SeatIn(player),ActorRef.noSender());
                    }
                    break;
                }
                return null;
            }
            else if(gameMessage.getType()== GameMessage.MessageType.ACTION){
                ActorSelection gameActor = system.actorSelection(gameActorPath);
                SessionMessage sessionMessage = new SessionMessage();
                sessionMessage.session=userSession;
                sessionMessage.name=userName;
                sessionMessage.gameMessage=gameMessage;
                ActionMessage actionMessage = new ActionMessage(ActionMessage.Cmd.ADD);
                actionMessage.setSessionMessage(sessionMessage);
                gameActor.tell(actionMessage, ActorRef.noSender());
                return null;
            }
        }
        return gameMessage;
    }
}
