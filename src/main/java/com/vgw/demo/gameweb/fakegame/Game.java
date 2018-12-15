package com.vgw.demo.gameweb.fakegame;

import com.vgw.demo.gameweb.message.GameMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Queue;

@Component
@Scope("prototype")
public class Game extends Thread{

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    Queue<GameMessage>  gameMessages;

    public enum GameState
    {
        WAIT,READY, START, RESULT,CLOSE
    };

    private int loopCnt =0;

    protected GameState gameState = GameState.WAIT;

    private Table table;

    private int wiinerCard;
    private int betAmmount;
    private int turnSeq;
    private int maxTurn;

    public void setTable(Table table){
        this.table = table;
        turnSeq=0;
        maxTurn=2;
        gameMessages = new ArrayDeque<>();
        chkGame(false);
        betAmmount=10;
    }

    protected boolean isStartGame(){
        boolean hasNext = false;
        if(gameState == GameState.WAIT){
            if( table.getSeatCnt() > table.getMinPly()-1 ){
                gameState = GameState.READY;
                hasNext = true;
            }
        }else{
            if( table.getSeatCnt() > table.getMinPly()-1 ){
                gameState = GameState.WAIT;
                hasNext = false;
            }
        }
        return hasNext;
    }


    protected void readyCard(){
        wiinerCard=1;
        turnSeq=1;
    }

    protected void betting(){

    }

    protected void turn(int turnSeq){
        this.turnSeq=turnSeq;

        if(turnSeq==0){

        }else if(turnSeq==1){

        }

    }

    protected void gameResult(){
        gameState=GameState.RESULT;
    }

    protected void waitTime(int time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
        }
    }

    protected GameMessage waitForAction(Player ply,int waitTime){
        GameMessage action = null;
        for(int i=0;i<waitTime;i++){
            GameMessage peekMsg=gameMessages.peek();
            if(peekMsg.getSender()==ply.getSession() && peekMsg.getType().equals("GAME") ){
                gameProcess(peekMsg);
                action=peekMsg;
                break;
            }else {
                otherProcess(peekMsg);
            }
            waitTime(waitTime);

        }
        return action;
    }

    protected void otherProcess(GameMessage gameMessage){
    }

    protected void gameProcess(GameMessage gameMessage){

    }

    public void closeGame(){
        gameState=GameState.CLOSE;
    }

    @Override
    public void run() {
        try {

            while( true ){
                if(gameState==GameState.CLOSE) break;
                waitTime(100);
                if(loopCnt %100==0){
                    chkGame(false);
                }

                if(isStartGame() && loopCnt %10==0 ){
                    readyCard();
                    betting();
                    for(int turnCnt=0;turnCnt<maxTurn;turnCnt++){
                        turn(turnCnt);
                    }
                    gameResult();
                }
                loopCnt++;
                if(loopCnt ==1000000000) loopCnt =0;
            }
        }catch (Exception e){
            logger.error("ErrorGame:"+e.toString());
        }
    }


    protected void chkGame(boolean isDebug){
        if(isDebug)
            logger.debug(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
        else
            logger.info(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
    }

    protected void sendAll(@Payload GameMessage gameMessage){
        for(Player ply:table.getPlayList()){
            send(ply,gameMessage);
        }
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
