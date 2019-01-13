package com.vgw.demo.gameweb.controler.rest;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.actor.GameTick;
import com.vgw.demo.gameweb.message.actor.PlayerList;
import com.vgw.demo.gameweb.message.actor.TableInfo;
import com.vgw.demo.gameweb.util.AkkaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameInfoController {

    private static final Logger logger = LoggerFactory.getLogger(GameInfoController.class);

    @Autowired
    private ActorSystem system;

    @RequestMapping("/gameinfo/dealer/{gameId}")
    int getDealer(@PathVariable String gameId) {
        try{
            String actorPath = "user/lobby/table-" + gameId;
            ActorSelection tableActor = system.actorSelection(actorPath);
            return (int)AkkaUtil.AskToActorSelect(tableActor,new TableInfo(TableInfo.Cmd.DealerPos),1);
        }catch (Exception e){
            logger.error(e.toString());
            return -100;
        }
    }

    @RequestMapping("/gameinfo/players/{gameId}")
    List<Player> getPlayers(@PathVariable String gameId) {
        try{
            String actorPath = "user/lobby/table-" + gameId;
            ActorSelection tableActor = system.actorSelection(actorPath);
            return (List<Player>)AkkaUtil.AskToActorSelect(tableActor,new PlayerList(PlayerList.Cmd.PLAYER),1);
        }catch (Exception e){
            logger.error(e.toString());
            return null;
        }
    }

    @RequestMapping("/gameinfo/ping/{gameId}")
    String testPing(@PathVariable String gameId) {
        try{
            String actorPath = "user/lobby/table-" + gameId;
            ActorSelection tableActor = system.actorSelection(actorPath);
            return (String)AkkaUtil.AskToActorSelect(tableActor,new GameTick(GameTick.Cmd.TESTPING),1);
        }catch (Exception e){
            logger.error(e.toString());
            return "something wrong";
        }
    }

}
