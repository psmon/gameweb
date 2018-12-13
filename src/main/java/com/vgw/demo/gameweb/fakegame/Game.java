package com.vgw.demo.gameweb.fakegame;

import com.vgw.demo.gameweb.message.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class Game extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    @Autowired
    private TaskExecutor taskExecutor;

    public enum GameState
    {
        WAIT,READY, START, RESULT,CLOSE
    };

    private int msgCnt=0;

    protected GameState gameState = GameState.WAIT;

    private Table table;

    private List<Player> player = new ArrayList<>();


    public void setTable(Table table){
        this.table = table;
        chkGame(false);
    }

    @Override
    public void run() {
        try {

            while( true ){
                if(gameState==GameState.CLOSE) break;

                Thread.sleep(1000);
                if(msgCnt%10==0){
                    chkGame(true);
                }

                msgCnt++;
                if(msgCnt==1000000000) msgCnt =0;

            }

        }catch (Exception e){
        }
    }


    protected void chkGame(boolean isDebug){
        if(isDebug)
            logger.debug(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
        else
            logger.info(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
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
