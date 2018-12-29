package com.vgw.demo.gameweb.thread;

import com.vgw.demo.gameweb.gameobj.Player;
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
@SuppressWarnings("Duplicates")
public class Game extends Thread{

    public enum GameState
    {
        WAIT,START,READY, CARD1,BET,TURN1,TURN2 ,RESULT,CLOSE
    }

    private Queue<SessionMessage> actionMessage;
    private List<Integer>  gameCard;

    private int loopCnt =0;
    private GameState gameState = GameState.WAIT;
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

    private boolean isStartGame(){
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

    private void readyCard(){
        gameState=GameState.CARD1;
        wiinerCard=1;
        turnSeq=1;
        int playNum = table.getPlayList(true).size();
        gameCard.clear();
        Random random = new Random();
        int maxCard=7;

        wiinerCard = random.nextInt(maxCard);
        List<Integer> otherCards = new ArrayList<>();
        //Simbple Card Generator
        while (true){
            int otherCard = random.nextInt(maxCard);
            if(wiinerCard!=otherCard) otherCards.add(otherCard);
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
        for(Player ply:table.getPlayList(true)){
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

    private void stagestart(){
        gameState=GameState.START;
        totalBetAmmount=0;
        GameMessage message = new GameMessage();
        message.setType(GameMessage.MessageType.GAME);
        message.setContent("stagestart");
        sendAll(message);
        table.setNextDealer();
        updateDealer(table.getDealer());
    }

    private void betting(){
        gameState=GameState.BET;
        float aniDelay=0.0f;
        for(Player ply:table.getPlayList(true)){
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

    private void updateDealer(int seat){
        GameMessage indicator = new GameMessage();
        indicator.setType(GameMessage.MessageType.GAME);
        indicator.setContent("dealer");
        indicator.setSeatno(seat);
        sendAll(indicator);
    }

    private void indicator(int focusSeat){
        //indicator
        GameMessage indicator = new GameMessage();
        indicator.setType(GameMessage.MessageType.GAME);
        indicator.setContent("indicator");
        indicator.setSeatno(focusSeat);
        sendAll(indicator);
    }

    private void swapcard(Player ply,int targetSeatNo){
        int srcSeatNo=ply.getSeatNo();
        //int targetSeatNo=actionRes.getNum1();
        int tmpcard = gameCard.get(srcSeatNo);
        gameCard.set(srcSeatNo, gameCard.get(targetSeatNo));
        gameCard.set(targetSeatNo,tmpcard);

        swapCard(srcSeatNo,targetSeatNo);
        waitForAni(3000);
        Player targetPly = table.findUser(targetSeatNo);
        changedCard(ply,targetPly);

    }

    private void reqAction(){
        int timeBank=12;
        int idx=0;
        boolean bBotMode=true;
        for(Player ply:table.getPlayList(true)){
            if(idx>0 && bBotMode)   timeBank=8; //Test for AI Action
            idx=idx+1;
            GameMessage actionReq = new GameMessage();
            actionReq.setType(GameMessage.MessageType.GAME);
            actionReq.setContent("action");
            send(ply,actionReq);

            indicator(ply.getSeatNo());

            GameMessage actionRes = waitForAction(ply,timeBank);

            boolean isChangeCard=false;
            if(actionRes!=null){
                if(actionRes.getContent().equals("change")){
                    int targetSeatNo=actionRes.getNum1();
                    swapcard(ply,targetSeatNo);
                    isChangeCard=true;
                }
            }
            if(!isChangeCard){
                if(!bBotMode){
                    waitForAni(1000);
                    GameMessage timeOutMessage=new GameMessage();
                    timeOutMessage.setType(GameMessage.MessageType.GAME);
                    timeOutMessage.setContent("actionend");
                    send(ply,timeOutMessage);
                }else{
                    // Auto Change:AI MODE...
                    List<Integer> otherUsers = new ArrayList<>();
                    for(Player otherPly:table.getPlayList(true)){
                        if(otherPly.getSeatNo()!=ply.getSeatNo())
                            otherUsers.add(otherPly.getSeatNo());
                    }
                    Collections.shuffle(otherUsers);
                    int targetSeatNo=otherUsers.get(0);
                    swapcard(ply,targetSeatNo);
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private void changedCard(Player srcPly,Player targetPly){
        GameMessage changedInfo = new GameMessage();
        changedInfo.setType(GameMessage.MessageType.GAME);
        changedInfo.setContent("changed");
        changedInfo.setSeatno(srcPly.getSeatNo());
        changedInfo.setNum1( gameCard.get(srcPly.getSeatNo()));
        changedInfo.setNum2( targetPly.getSeatNo() );
        GameMessage changedInfo2 = new GameMessage();
        changedInfo2.setType(GameMessage.MessageType.GAME);
        changedInfo2.setContent("changed");
        changedInfo2.setSeatno(targetPly.getSeatNo());
        changedInfo2.setNum1( gameCard.get(targetPly.getSeatNo()));
        changedInfo2.setNum2( srcPly.getSeatNo() );

        send(srcPly,changedInfo);
        send(targetPly,changedInfo2);
    }

    private void swapCard(int srcSeatNo,int targetSeatNo){
        //Public Changed Info
        GameMessage changedInfo = new GameMessage();
        changedInfo.setType(GameMessage.MessageType.GAME);
        changedInfo.setContent("swapcard");
        changedInfo.setSeatno(0);
        changedInfo.setNum1( srcSeatNo );
        changedInfo.setNum2( targetSeatNo );
        sendAll(changedInfo);
    }

    private void turn(int turnSeq){
        this.turnSeq=turnSeq;
        actionMessage.clear();
        logger.info("===== turn:"+turnSeq);
        if(turnSeq==0){
            reqAction();
        }else if(turnSeq==1){
            reqAction();
        }
    }

    private void showAllCards(){
        gameState=GameState.RESULT;
        float delayAni=0.0f;
        for(Player ply:table.getPlayList(true)){
            int card=gameCard.get(ply.getSeatNo());
            GameMessage gameMessage = new GameMessage();
            gameMessage.setContent("opencard");
            gameMessage.setType(GameMessage.MessageType.GAME);
            gameMessage.setSeatno(ply.getSeatNo());
            gameMessage.setNum1(card);
            gameMessage.setDelay(delayAni);
            delayAni+=0.2f;
            sendAll(gameMessage);
        }
    }

    private void gameResult(){
        gameState=GameState.RESULT;
        Player winPly=null;
        //TODO: gamecard to plycard
        for(Player ply:table.getPlayList(true)){
            if( gameCard.get(ply.getSeatNo())==wiinerCard){
                winPly=ply;
                ply.updateChips(totalBetAmmount);
                break;
            }
        }
        GameMessage gameMessage = new GameMessage();
        gameMessage.setContent("gameresult");
        gameMessage.setSeatno(winPly.getSeatNo());
        gameMessage.setNum1(winPly.getChips());
        gameMessage.setType(GameMessage.MessageType.GAME);
        sendAll(gameMessage);
        waitForAni(5000); //Result Time..
    }

    private void waitForAni(int time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
        }
    }

    private GameMessage waitForAction(Player ply,int waitCnt){
        GameMessage action = null;
        for(int i=0;i<waitCnt;i++){
            SessionMessage peekMsg= actionMessage.peek();
            if(peekMsg!=null){
                if(peekMsg.session.equals(ply.getSession())){
                    gameProcess(peekMsg);
                    action=peekMsg.gameMessage;
                    break;
                }else {
                    otherProcess(peekMsg);
                }
            }
            waitForAni(1000);
        }
        actionMessage.clear();
        return action;
    }

    private void otherProcess(SessionMessage gameMessage){
    }

    private void gameProcess(SessionMessage gameMessage){

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
                    waitForAni(100);
                    if(loopCnt %100==0){
                        chkGame(false);
                    }

                    if(isStartGame() && loopCnt %10==0 ){
                        stagestart();
                        int playingCnt = table.getPlayList(false).size();
                        logger.info("Game Bet Card");
                        waitForAni(1000);
                        betting();
                        waitForAni(3000 + (playingCnt*1000) );
                        logger.info("Game Ready Card");
                        readyCard();
                        for(int turnCnt=0;turnCnt<maxTurn;turnCnt++){
                            turn(turnCnt);
                            waitForAni(1000);
                        }
                        logger.info("Game ShowDown");
                        showAllCards();
                        waitForAni(3000 + (playingCnt*1000) );
                        logger.info("Game Winner Info");
                        gameResult();
                        waitForAni(5000);
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


    private void sendSeatInfo(Player ply,Boolean isAll,Player target){
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
        for(Player other:table.getPlayList(false)){
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
        for(Player other:table.getPlayList(false)){
            sendSeatInfo(other,false,ply);
        }
        //For Test
        //testDemoPacket(ply);
    }

    protected void OnError(Player ply,String errorMsg){
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.ERROR);
        gameMessage.setContent("error!!"+errorMsg);
        send(ply,gameMessage);
    }


    private void chkGame(boolean isDebug){
        if(isDebug)
            logger.debug(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
        else
            logger.info(String.format("GameState:%s Tableid:%d",gameState.toString(),table.getTableId() ));
    }

    private void sendAll(@Payload GameMessage gameMessage){
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
