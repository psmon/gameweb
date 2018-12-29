package com.vgw.demo.gameweb.thread;

import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// TODO : More Clear using Depency Injection
// https://blog.marcnuri.com/field-injection-is-not-recommended/

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Lobby {

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    private final Map<Integer,Table> gameTables;

    static private Map<String,SimpMessageSendingOperations>    sessionMgr = new HashMap<>();

    static SimpMessageSendingOperations getSender(String sessionid){
        return sessionMgr.get(sessionid);
    }

    @Autowired
    public  Lobby(){
        gameTables = new HashMap<>();
        // Create
        for(int i=0;i<5;i++){
            Table addTable = new Table();
            int autoTableID = i+1;
            addTable.setTableId(autoTableID);
            gameTables.put(autoTableID,addTable);
        }
    }

    public void addSender(String sessionid,SimpMessageSendingOperations sender){
        sessionMgr.put(sessionid,sender);
    }

    public void removeSender(String sessionid){
        for (Map.Entry<Integer, Table> entry : gameTables.entrySet()) {
            entry.getValue().cleanUser(sessionid);
        }
        sessionMgr.remove(sessionid);
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

    public void joinGameTable(int tableId,String name,String session){
        Player ply = new Player();
        ply.setName(name);
        ply.setSession(session);
        gameTables.get(tableId).joinTable(ply);
    }

    public Table getTable(int tableId){
        return gameTables.get(tableId);
    }

}
