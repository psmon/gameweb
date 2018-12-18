package com.vgw.demo.gameweb.fakegame;

import com.vgw.demo.gameweb.controler.WebSocketEventListener;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.SessionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import java.util.*;

@Component
@Scope("prototype")
public class Game extends Thread{

    public enum GameState
    {
        WAIT,READY, START,BET,TURN1,TURN2 ,RESULT,CLOSE
    };

    Queue<SessionMessage>  gameMessages;
    Queue<Integer>  gameCard;

    private int loopCnt =0;
    protected GameState gameState = GameState.WAIT;
    private Table table;

    private int wiinerCard;
    private int betAmmount;
    private int totalBetAmmount;
    private int turnSeq;
    private int maxTurn;

    private static final Logger logger = LoggerFactory.getLogger(Lobby.class);

    public void addGameMessage(SessionMessage msg){
        gameMessages.add(msg);
    }

    protected SessionMessage peekGameMessage(){
        if(gameMessages.size()>0)
            return gameMessages.peek();
        else
            return null;
    }

    public void setTable(Table table){
        //messagingTemplate = WebSocketEventListener.getSender();
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
        int playNum = table.getPlayList().size();
        gameCard.clear();

        Random random = new Random();
        wiinerCard = random.nextInt(8)+1;
        List<Integer> otherCards = new ArrayList<>();
        //Simbple Card Generator
        while (true){
            Integer otherCArd = random.nextInt(8)+1;
            if(wiinerCard!=otherCArd) otherCards.add(otherCArd);
            if(otherCards.size()==3) break;
        }
        // Card Split
        // 3 = 1,2
        // 4 = 1,3
        // 5 = 1,2,2
        // 6 = 1,2,3
        // 7 = 1,2,2,2
        switch (playNum){

        }



    }

    protected void betting(){
        float aniDelay=0.0f;
        for(Player ply:table.getPlayList()){
            ply.updateChips(-betAmmount);
            GameMessage message = new GameMessage();
            message.setSeatno(ply.getSeatNo());
            message.setContent("bet");
            message.setDelay(aniDelay);
            message.setNum1(betAmmount);
            message.setNum2(ply.getChips());
            aniDelay+=0.03f;
            // Todo: SeatOut for LoseMoney
            send(ply,message);
        }
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
            SessionMessage peekMsg=gameMessages.peek();
            if(peekMsg.session==ply.getSession() && peekMsg.gameMessage.equals("GAME") ){
                gameProcess(peekMsg);
                action=peekMsg.gameMessage;
                break;
            }else {
                otherProcess(peekMsg);
            }
            waitTime(waitTime);

        }
        return action;
    }

    protected void otherProcess(SessionMessage gameMessage){
    }

    protected void gameProcess(SessionMessage gameMessage){

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

    private void testDemoPacket(Player ply){
        GameMessage reusePacket = new GameMessage();
        reusePacket.setType(GameMessage.MessageType.GAME);
        // Seat User
        for(int idx=0;idx<5;idx++){
            reusePacket = new GameMessage();
            reusePacket.setType(GameMessage.MessageType.GAME);
            reusePacket.setContent("seat");
            reusePacket.setSender("psmon-"+idx);
            reusePacket.setSeatno(idx);
            reusePacket.setNum1(500+idx);
            send(ply,reusePacket);
        }

        float delayTotal=1.0f;
        // Move Dealer
        reusePacket = new GameMessage();
        reusePacket.setType(GameMessage.MessageType.GAME);
        reusePacket.setDelay(delayTotal);
        reusePacket.setContent( String.format("dealer"));
        reusePacket.setSeatno(2);
        send(ply,reusePacket);

        // Auto Bet
        for(int idx=0;idx<5;idx++){
            delayTotal+=0.5f;
            reusePacket = new GameMessage();
            reusePacket.setType(GameMessage.MessageType.GAME);
            reusePacket.setContent( String.format("bet"));
            reusePacket.setSeatno(idx);
            reusePacket.setNum1(30);
            reusePacket.setDelay(delayTotal);
            send(ply,reusePacket);
        }

        // Card
        for(int idx=0;idx<5;idx++){
            delayTotal+=0.3f;
            reusePacket = new GameMessage();
            reusePacket.setType(GameMessage.MessageType.GAME);
            reusePacket.setContent( String.format("card"));
            reusePacket.setSeatno(idx);
            reusePacket.setNum1(0); //Back-Card
            reusePacket.setDelay(delayTotal);
            send(ply,reusePacket);
        }
    }

    protected void OnSeatPly(Player ply){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("seat" );
        gameMessage.setSeatno(ply.getSeatNo());
        gameMessage.setNum1(ply.getChips());
        gameMessage.setSender(ply.getName());

        sendAll(gameMessage);
    }

    protected void OnConnectPly(Player ply){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("readytable");
        gameMessage.setNum1(table.getTableId());
        send(ply,gameMessage);
        //For Test
        testDemoPacket(ply);
    }

    public void OnError(Player ply,String errorMsg){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.ERROR);
        gameMessage.setContent("error!!"+errorMsg);
        send(ply,gameMessage);
    }


    protected void chkGame(boolean isDebug){
        if(isDebug)
            logger.debug(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
        else
            logger.info(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
    }

    protected void sendAll(@Payload GameMessage gameMessage){
        for(Player ply:table.viewList){
            send(ply,gameMessage);
        }
    }

    protected void send(Player player,@Payload GameMessage gameMessage){
        SimpMessageSendingOperations messagingTemplate = Lobby.getSender(player.getSession());
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(player.getSession());
        headerAccessor.setLeaveMutable(true);
        GameMessage gameMessage2 = new GameMessage();
        gameMessage2.setType(GameMessage.MessageType.GAME);
        messagingTemplate.convertAndSendToUser(player.getSession(),"/topic/public",gameMessage );
    }
}
