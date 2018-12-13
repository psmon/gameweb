package com.vgw.demo.gameweb.fakegame;

import java.util.HashMap;
import java.util.Map;

import com.vgw.demo.gameweb.message.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.xml.ws.Service;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Lobby {

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Map<Integer,Table> gameTables;

    public  Lobby(){
        gameTables = new HashMap<Integer, Table>();
        // Create
        for(int i=0;i<5;i++){
            Table addTable = new Table();
            int autoTableID = i+1;
            addTable.setTableId(autoTableID);
            gameTables.put(autoTableID,addTable);
        }
    }

    public Table findTableByID(Integer tableId){
        return gameTables.get(tableId);
    }

    public Game findGameByTableID(Integer tableId){
        return gameTables.get(tableId).getGame();
    }

    public GameMessage sayHello(GameMessage message){
        GameMessage msg = new GameMessage();
        msg.setSender("admin");
        msg.setContent(message.getContent());
        msg.setType(GameMessage.MessageType.CHAT);
        return msg;
    }

}
