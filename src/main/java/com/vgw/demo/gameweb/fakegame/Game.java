package com.vgw.demo.gameweb.fakegame;

import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.SessionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class Game extends Thread{

    public enum GameState
    {
        WAIT,START,READY, CARD1,BET,TURN1,TURN2 ,RESULT,CLOSE
    };

    Queue<SessionMessage> actionMessage;
    List<Integer>  gameCard;

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
        actionMessage.add(msg);
    }

    protected SessionMessage peekGameMessage(){
        if(actionMessage.size()>0)
            return actionMessage.peek();
        else
            return null;
    }

    public void setTable(Table table){
        //messagingTemplate = WebSocketEventListener.getSender();
        this.table = table;
        turnSeq=0;
        maxTurn=2;
        actionMessage = new ArrayDeque<>();
        gameCard = new ArrayList<>();
        chkGame(false);
        betAmmount=10;
    }

    protected boolean isStartGame(){
        boolean hasNext = false;
        if( gameState == GameState.WAIT ){
            if( table.getSeatCnt() > table.getMinPly()-1 ){
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
        gameState=GameState.CARD1;
        wiinerCard=1;
        turnSeq=1;
        int playNum = table.getPlayList().size();
        gameCard.clear();
        Random random = new Random();
        int maxCard=7;

        wiinerCard = random.nextInt(maxCard);
        List<Integer> otherCards = new ArrayList<>();
        //Simbple Card Generator
        while (true){
            Integer otherCArd = random.nextInt(maxCard);
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
            case 2: // for Only Test
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
            case 3:
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                break;
            case 4:
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                break;
            case 5:
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                break;
            case 6:
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                break;
            case 7:
                gameCard.add(wiinerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(2));
                gameCard.add(otherCards.get(2));
                break;
        }
        int seedValue = 10;
        Collections.shuffle(gameCard, new Random(seedValue));

        float aniDelay=0.0f;
        for(Player ply:table.getPlayList()){
            int playerCard = gameCard.get(ply.getSeatNo());
            ply.setCard( playerCard );

            GameMessage sendCardInfo = new GameMessage();
            sendCardInfo.setSeatno(ply.getSeatNo());
            sendCardInfo.setContent("card");
            sendCardInfo.setType(GameMessage.MessageType.GAME);
            sendCardInfo.setNum1(0);
            sendCardInfo.setDelay(aniDelay);
            sendAll(sendCardInfo);

            GameMessage sendMyCard = new GameMessage();
            sendMyCard.setSeatno(ply.getSeatNo());
            sendMyCard.setContent("showcard");
            sendMyCard.setType(GameMessage.MessageType.GAME);
            sendMyCard.setNum1(playerCard);
            sendMyCard.setDelay(5);
            send(ply,sendMyCard);
            aniDelay+=0.3f;
        }
    }

    protected void stagestart(){
        gameState=GameState.START;
        totalBetAmmount=0;
        GameMessage message = new GameMessage();
        message.setType(GameMessage.MessageType.GAME);
        message.setContent("stagestart");
        sendAll(message);
    }

    protected void betting(){
        gameState=GameState.BET;
        float aniDelay=0.0f;
        for(Player ply:table.getPlayList()){
            ply.updateChips(-betAmmount);
            GameMessage message = new GameMessage();
            message.setType(GameMessage.MessageType.GAME);
            message.setSeatno(ply.getSeatNo());
            message.setContent("bet");
            message.setDelay(aniDelay);
            message.setNum1(betAmmount);
            message.setNum2(ply.getChips());
            totalBetAmmount+=betAmmount;
            aniDelay+=0.3f;
            // Todo: SeatOut for LoseMoney
            //send(ply,message);
            sendAll(message);
        }
    }

    protected void reqAction(){
        int timeBank=5;
        int idx=0;
        for(Player ply:table.getPlayList()){
            if(idx>0)   timeBank=3; //Test...
            idx=idx+1;
            GameMessage actionReq = new GameMessage();
            actionReq.setType(GameMessage.MessageType.GAME);
            actionReq.setContent("action");
            send(ply,actionReq);

            //indicator
            GameMessage indicator = new GameMessage();
            indicator.setType(GameMessage.MessageType.GAME);
            indicator.setContent("indicator");
            sendAll(indicator);

            GameMessage actionRes = waitForAction(ply,timeBank);

            boolean isChangeCard=false;
            if(actionRes!=null){
                if(actionRes.getContent().equals("change")){
                    int srcSeatNo=ply.getSeatNo();
                    int targetSeatNo=actionRes.getNum1();
                    int tmpcard = gameCard.get(srcSeatNo);
                    gameCard.set(srcSeatNo, gameCard.get(targetSeatNo));
                    gameCard.set(targetSeatNo,tmpcard);
                    isChangeCard=true;

                    GameMessage changedInfo = new GameMessage();
                    changedInfo.setType(GameMessage.MessageType.GAME);
                    changedInfo.setContent("changed");
                    changedInfo.setSeatno(srcSeatNo);
                    changedInfo.setNum1( gameCard.get(srcSeatNo));
                    changedInfo.setNum2( targetSeatNo );
                    send(ply,changedInfo);

                    Player targetPly = table.findUser(targetSeatNo);
                    changedInfo = new GameMessage();
                    changedInfo.setType(GameMessage.MessageType.GAME);
                    changedInfo.setContent("changed");
                    changedInfo.setSeatno(targetSeatNo);
                    changedInfo.setNum1( gameCard.get(targetSeatNo));
                    changedInfo.setNum2( ply.getSeatNo() );
                    send(targetPly,changedInfo);

                    //Public Changed Info
                    changedInfo = new GameMessage();
                    changedInfo.setType(GameMessage.MessageType.GAME);
                    changedInfo.setContent("swapcard");
                    changedInfo.setSeatno(0);
                    changedInfo.setNum1( srcSeatNo );
                    changedInfo.setNum2( targetSeatNo );
                    sendAll(changedInfo);
                }
            }
            if(!isChangeCard){
                GameMessage timeOutMessage=new GameMessage();
                timeOutMessage.setType(GameMessage.MessageType.GAME);
                timeOutMessage.setContent("actionend");
                send(ply,timeOutMessage);
            }
        }
    }

    protected void turn(int turnSeq){
        this.turnSeq=turnSeq;
        actionMessage.clear();
        logger.info("===== turn:"+turnSeq);
        if(turnSeq==0){
            reqAction();
        }else if(turnSeq==1){
            reqAction();
        }
    }

    protected void gameResult(){
        gameState=GameState.RESULT;
        GameMessage gameMessage = new GameMessage();
        gameMessage.setContent("gameresult");
        gameMessage.setType(GameMessage.MessageType.GAME);
        //Todo: GameResult
        sendAll(gameMessage);
        waitTime(7000); //Result Time..
    }

    protected void waitTime(int time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
        }
    }

    protected GameMessage waitForAction(Player ply,int waitCnt){
        GameMessage action = null;
        for(int i=0;i<waitCnt;i++){
            SessionMessage peekMsg= actionMessage.peek();
            if(peekMsg!=null){
                if(peekMsg.session==ply.getSession() ){
                    gameProcess(peekMsg);
                    action=peekMsg.gameMessage;
                    break;
                }else {
                    otherProcess(peekMsg);
                }
            }
            waitTime(1000);
        }
        actionMessage.clear();
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
                try{
                    if(gameState==GameState.CLOSE) break;
                    waitTime(100);
                    if(loopCnt %100==0){
                        chkGame(false);
                    }

                    if(isStartGame() && loopCnt %10==0 ){
                        stagestart();
                        logger.info("Game Bet Card");
                        waitTime(1000);
                        betting();
                        waitTime(5000);
                        logger.info("Game Ready Card");
                        readyCard();
                        for(int turnCnt=0;turnCnt<maxTurn;turnCnt++){
                            turn(turnCnt);
                        }
                        waitTime(5000);
                        gameResult();
                        waitTime(5000);
                        gameState=GameState.WAIT;
                    }
                }catch (Exception e){
                    logger.error("Error-ResumeGame:"+e.toString());
                    gameState=GameState.WAIT;
                }
                loopCnt++;
                if(loopCnt ==1000000000) loopCnt =0;
            }
        }catch (Exception e){
            logger.error("Error-Skip..Game:"+e.toString());
            gameState=GameState.CLOSE;
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


    protected void sendSeatInfo(Player ply,Boolean isAll,Player target){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("seat" );
        gameMessage.setSeatno(ply.getSeatNo());
        gameMessage.setNum1(ply.getChips());
        gameMessage.setSender(ply.getName());
        if(isAll)
            sendAll(gameMessage);
        else
            send(target,gameMessage);
    }

    protected void OnSeatPly(Player ply){
        sendSeatInfo(ply,true,null);
        for(Player other:table.getPlayList()){
            if(!ply.getSession().equals(other.getSession())){
                sendSeatInfo(other,false,ply);
            }
        }
    }

    protected void OnSeatOutPly(Player ply){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("seatout" );
        gameMessage.setSeatno(ply.getSeatNo());
        gameMessage.setSender(ply.getName());
        sendAll(gameMessage);
    }

    protected void OnConnectPly(Player ply){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("readytable");
        gameMessage.setNum1(table.getTableId());
        send(ply,gameMessage);
        for(Player other:table.getPlayList()){
            sendSeatInfo(other,false,ply);
        }
        //For Test
        //testDemoPacket(ply);
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
