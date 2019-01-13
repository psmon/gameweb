package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.actor.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static akka.pattern.Patterns.ask;

// TODO : It should be improved to FSM pattern for save game state as a non-block-king
// Link : https://doc.akka.io/docs/akka/2.4/java/lambda-fsm.html

//GameCORE - Changes Cards to get Unique cards
@SuppressWarnings("Duplicates")
public class GUCGameActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef gameActor;
    private int winnerCard;
    private int betAmount;
    private int totalBetAmmount;
    private int turnSeq;
    private int maxTurn;
    private GameActor.GameState gameState = GameActor.GameState.WAIT;
    private List<Integer> gameCard;
    private GameSend gameSend;

    static public Props props(GameSend gameSend) {
        return Props.create(GUCGameActor.class, () -> new GUCGameActor(gameSend));
    }

    GUCGameActor(GameSend gameSend) {
        this.gameSend = gameSend;
        gameActor = getContext().getParent();
        turnSeq=0;
        maxTurn=2;
        gameCard = new ArrayList<>();
        betAmount =10;
    }

    protected void runGameStage(){
        try{
            stagestart();
            int playingCnt = (int)gameSend.askToTable(new TableInfo(TableInfo.Cmd.SeatCnt));
            log.info("Game Bet Card");
            waitForAni(1000);
            betting();
            waitForAni(3000 + (playingCnt*1000) );
            log.info("Game Ready Card");
            readyCard();
            for(int turnCnt=0;turnCnt<maxTurn;turnCnt++){
                turn(turnCnt);
                waitForAni(1000);
            }
            log.info("Game ShowDown");
            showAllCards();
            waitForAni(3000 + (playingCnt*1000) );
            log.info("Game Winner Info");
            gameResult();
            gameState= GameActor.GameState.WAIT;

        }catch (Exception e){
            log.error("Error-ResumeGame:"+e.toString());
            gameState= GameActor.GameState.WAIT;
        }
        gameActor.tell(new GameStateInfo(gameState),ActorRef.noSender());

    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(GameStateInfo.class, c -> {
                    if(c.getGameState()== GameActor.GameState.START){
                        gameState = c.getGameState();
                        runGameStage();
                    }
                })
                .build();
    }

    private void waitForAni(int time){
        // Dealy strategy for  Animation
        // - Thread Sleep ( Here )
        // - Send Delay By Server : gameSend.sendAll(gameMessage,5000 <--delay  );
        // - Delay By Client : gameMessage.setDelay(aniDelay);
        try{
            Thread.sleep(time);
        }catch (Exception e){
        }
    }

    protected void readyCard() throws Exception {
        gameState= GameActor.GameState.CARD1;
        winnerCard =1;
        turnSeq=1;
        List<Player> playList = (List<Player>)gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER_DEALER_ORDER));

        int playNum = playList.size();
        gameCard.clear();
        Random random = new Random();
        int maxCard=7;

        winnerCard = random.nextInt(maxCard);
        List<Integer> otherCards = new ArrayList<>();
        //Simbple Card Generator
        while (true){
            Integer otherCArd = random.nextInt(maxCard);
            if(winnerCard !=otherCArd) otherCards.add(otherCArd);
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
                gameCard.add(winnerCard);
                gameCard.add(otherCards.get(0));
            case 3:
                gameCard.add(winnerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                break;
            case 4:
                gameCard.add(winnerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                break;
            case 5:
                gameCard.add(winnerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                break;
            case 6:
                gameCard.add(winnerCard);
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(0));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                gameCard.add(otherCards.get(1));
                break;
            case 7:
                gameCard.add(winnerCard);
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
        for(Player ply:playList){
            int playerCard = gameCard.get(ply.getSeatNo());
            ply.setCard( playerCard );

            GameMessage sendCardInfo = new GameMessage();
            sendCardInfo.setSeatno(ply.getSeatNo());
            sendCardInfo.setContent("card");
            sendCardInfo.setType(GameMessage.MessageType.GAME);
            sendCardInfo.setNum1(0);
            sendCardInfo.setDelay(aniDelay);
            gameSend.sendAll(sendCardInfo);

            GameMessage sendMyCard = new GameMessage();
            sendMyCard.setSeatno(ply.getSeatNo());
            sendMyCard.setContent("showcard");
            sendMyCard.setType(GameMessage.MessageType.GAME);
            sendMyCard.setNum1(playerCard);
            sendMyCard.setDelay(5);
            gameSend.send(ply,sendMyCard);
            aniDelay+=0.3f;
        }
    }

    protected void stagestart() throws Exception {
        gameState= GameActor.GameState.START;
        totalBetAmmount=0;
        GameMessage message = new GameMessage();
        message.setType(GameMessage.MessageType.GAME);
        message.setContent("stagestart");
        gameSend.sendAll(message);
        Integer netxtDealer = (Integer)gameSend.askToTable(new TableInfo(TableInfo.Cmd.DealerNext));
        updateDealer(netxtDealer);
    }

    protected void betting() throws Exception {
        gameState= GameActor.GameState.BET;
        float aniDelay=0.0f;
        List<Player> playList = (List<Player>)gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER_DEALER_ORDER));

        for(Player ply:playList){
            ply.updateChips(-betAmount);
            GameMessage message = new GameMessage();
            message.setType(GameMessage.MessageType.GAME);
            message.setSeatno(ply.getSeatNo());
            message.setContent("bet");
            message.setDelay(aniDelay);
            message.setNum1(betAmount);
            message.setNum2(ply.getChips());
            totalBetAmmount+= betAmount;
            aniDelay+=0.3f;
            // Todo: SeatOut for LoseMoney
            //send(ply,message);
            gameSend.sendAll(message);
        }
    }

    protected void updateDealer(int seat) throws Exception {
        GameMessage indicator = new GameMessage();
        indicator.setType(GameMessage.MessageType.GAME);
        indicator.setContent("dealer");
        indicator.setSeatno(seat);
        gameSend.sendAll(indicator);
    }

    protected void indicator(int focusSeat) throws Exception {
        //indicator
        GameMessage indicator = new GameMessage();
        indicator.setType(GameMessage.MessageType.GAME);
        indicator.setContent("indicator");
        indicator.setSeatno(focusSeat);
        gameSend.sendAll(indicator);
    }

    protected void swapCard(Player ply,int targetSeatNo) throws Exception {
        int srcSeatNo=ply.getSeatNo();
        //int targetSeatNo=actionRes.getNum1();
        int tmpcard = gameCard.get(srcSeatNo);
        gameCard.set(srcSeatNo, gameCard.get(targetSeatNo));
        gameCard.set(targetSeatNo,tmpcard);

        swapCard(srcSeatNo,targetSeatNo);
        waitForAni(3000);
        Player targetPly = (Player)gameSend.askToTable(new PlayerInfo(targetSeatNo));
        changedCard(ply,targetPly);
    }

    protected void swapCard(int srcSeatNo,int targetSeatNo) throws Exception {
        //Public Changed Info
        GameMessage changedInfo = new GameMessage();
        changedInfo.setType(GameMessage.MessageType.GAME);
        changedInfo.setContent("swapcard");
        changedInfo.setSeatno(0);
        changedInfo.setNum1( srcSeatNo );
        changedInfo.setNum2( targetSeatNo );
        gameSend.sendAll(changedInfo);
    }

    @SuppressWarnings("Duplicates")
    protected void changedCard(Player srcPly,Player targetPly){
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
        // delay send
        gameSend.send(srcPly,changedInfo);
        gameSend.send(targetPly,changedInfo2);
    }

    protected GameMessage waitForAction(Player ply,Integer timeBank){
        GameMessage result = null;
        try{
            Boolean isExpectMessage = false;
            Integer onceTimeOut = 2;
            Integer tryCnt = 1;
            Integer totalTimeOut = timeBank;
            FiniteDuration duration = FiniteDuration.create(onceTimeOut, TimeUnit.SECONDS);
            Timeout timeout = Timeout.durationToTimeout(duration);
            while (true){
                try{
                    Future<Object> future = ask(gameActor, new ActionMessage(ActionMessage.Cmd.PEEK), timeout);
                    result = (GameMessage)Await.result(future, timeout.duration());
                    // Simple Invalid Check for User Action
                    if(result.getSeatno()==ply.getSeatNo()){
                        isExpectMessage = true;
                    }
                }
                catch (TimeoutException ex){
                    log.info("wait for user message:"+tryCnt);
                }
                tryCnt++;
                totalTimeOut-=onceTimeOut;
                if(totalTimeOut < 0){
                    throw new Exception("TimeOut:Wait For Action");
                }
                if(isExpectMessage){
                    break;
                }
            }
        }catch (Exception e){
            log.info(e.getMessage());
            result =null;
        }

        return result;
    }

    protected void reqAction() throws Exception {
        int timeBank=12;
        int idx=0;
        boolean bBotMode=true;
        List<Player> playList = (List<Player>)gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER_DEALER_ORDER));

        for(Player ply:playList){
            if(idx>0 && bBotMode)   timeBank=8; //Test for AI Action
            idx=idx+1;
            GameMessage actionReq = new GameMessage();
            actionReq.setType(GameMessage.MessageType.GAME);
            actionReq.setContent("action");
            gameSend.send(ply,actionReq);

            indicator(ply.getSeatNo());

            GameMessage actionRes = waitForAction(ply,timeBank);

            boolean isChangeCard=false;
            if(actionRes!=null){
                if(actionRes.getContent().equals("change")){
                    int targetSeatNo=actionRes.getNum1();
                    swapCard(ply,targetSeatNo);
                    isChangeCard=true;
                }
            }
            if(!isChangeCard){
                if(!bBotMode){
                    waitForAni(1000);
                    GameMessage timeOutMessage=new GameMessage();
                    timeOutMessage.setType(GameMessage.MessageType.GAME);
                    timeOutMessage.setContent("actionend");
                    gameSend.send(ply,timeOutMessage);
                }else{
                    // Auto Change:AI MODE...
                    List<Integer> otherUsers = new ArrayList<>();
                    for(Player otherPly:playList){
                        if(otherPly.getSeatNo()!=ply.getSeatNo())
                            otherUsers.add(otherPly.getSeatNo());
                    }
                    Collections.shuffle(otherUsers);
                    int targetSeatNo=otherUsers.get(0);
                    swapCard(ply,targetSeatNo);
                }
            }
        }
    }


    protected void turn(int turnSeq) throws Exception {
        this.turnSeq=turnSeq;
        gameActor.tell(new ActionMessage(ActionMessage.Cmd.CLEAR),ActorRef.noSender());

        log.info("===== turn:"+turnSeq);
        if(turnSeq==0){
            reqAction();
        }else if(turnSeq==1){
            reqAction();
        }
    }

    protected void showAllCards() throws Exception {
        gameState= GameActor.GameState.RESULT;
        float delayAni=0.0f;
        List<Player> playList = (List<Player>)gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER_DEALER_ORDER));

        for(Player ply:playList){
            int card=gameCard.get(ply.getSeatNo());
            GameMessage gameMessage = new GameMessage();
            gameMessage.setContent("opencard");
            gameMessage.setType(GameMessage.MessageType.GAME);
            gameMessage.setSeatno(ply.getSeatNo());
            gameMessage.setNum1(card);
            gameMessage.setDelay(delayAni);
            delayAni+=0.2f;
            gameSend.sendAll(gameMessage);
        }
    }

    protected void gameResult() throws Exception {
        gameState= GameActor.GameState.RESULT;
        Player winPly=null;
        List<Player> playList = (List<Player>)gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER_DEALER_ORDER));

        //TODO: gamecard to plycard
        for(Player ply:playList){
            if( gameCard.get(ply.getSeatNo())== winnerCard){
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
        gameSend.sendAll(gameMessage);
        waitForAni(5000); //Result Time..
    }
}
