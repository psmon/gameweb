package com.vgw.demo.gameweb.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.SessionMessage;
import com.vgw.demo.gameweb.message.actor.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("Duplicates")
public class GameActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    // TODO : Improve Statement - https://doc.akka.io/docs/akka/2.4/java/fsm.html
    public enum GameState
    {
        WAIT,START,READY, CARD1,BET,TURN1,TURN2 ,RESULT,CLOSE
    }

    private int gameid;
    private int tickCnt;

    private GameState gameState = GameState.WAIT;
    private GameSend gameSend;

    private ActorRef  gameCore;

    private Queue<SessionMessage> actionMessage;
    private List<Integer>  gameCard;

    protected void addGameMessage(SessionMessage msg){
        actionMessage.add(msg);
    }

    protected SessionMessage peekGameMessage(){
        if(actionMessage.size()>0)
            return actionMessage.peek();
        else
            return null;
    }

    static public Props props(TableCreate tableCreate, ActorRef tableActor) {
        return Props.create(GameActor.class, () -> new GameActor(tableCreate, tableActor));
    }

    GameActor(TableCreate tableCreate, ActorRef tableActor) throws Exception {
        ActorSystem system = getContext().getSystem();
        this.gameid= tableCreate.getTableId();
        ActorSelection lobbySelect = this.getContext().actorSelection("/user/lobby");
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Future<ActorRef> fut = lobbySelect.resolveOne(duration);
        ActorRef lobbyActor = Await.result(fut, duration);
        gameSend = new GameSend(system,lobbyActor,tableActor);
        tickCnt=0;
        actionMessage=new ArrayDeque<>();
        gameCore = getContext().actorOf( CCUGameActor.props(gameSend),"core");
        log.info(String.format("Create Game:%d", tableCreate.getTableId()));
    }

    protected void seatPly(Player ply) throws Exception {
        List<Player> playList = (List<Player>) gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER));
        sendSeatInfo(ply,true,null);
        for(Player other:playList){
            if(!ply.getSession().equals(other.getSession())){
                sendSeatInfo(other,false,ply);
            }
        }
    }

    protected void seatOutPly(Player ply) throws Exception{
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("seatout" );
        gameMessage.setSeatno(ply.getSeatNo());
        gameMessage.setSender(ply.getName());
        gameSend.sendAll(gameMessage);
    }

    private void connectPly(Player ply) throws Exception {
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("readytable");
        gameMessage.setNum1(gameid);
        gameSend.send(ply,gameMessage);

        //Test Forward
        getSender().tell("joined",ActorRef.noSender());

        List<Player> playList = (List<Player>) gameSend.askToTable(new PlayerList(PlayerList.Cmd.PLAYER));
        for(Player other:playList){
            sendSeatInfo(other,false,ply);
        }
    }

    private void sendSeatInfo(Player ply,Boolean isAll,Player target) throws Exception {
        GameMessage gameMessage = new GameMessage();
        gameMessage.setType(GameMessage.MessageType.GAME);
        gameMessage.setContent("seat" );
        gameMessage.setSeatno(ply.getSeatNo());
        gameMessage.setNum1(ply.getChips());
        gameMessage.setSender(ply.getName());
        if(isAll)
            gameSend.sendAll(gameMessage);
        else
            gameSend.send(target,gameMessage);
    }

    protected boolean isStartGame() throws Exception {
        boolean hasNext = false;
        Integer seatCnt = (Integer) gameSend.askToTable(new TableInfo(TableInfo.Cmd.SeatCnt));
        Integer minPly = (Integer) gameSend.askToTable(new TableInfo(TableInfo.Cmd.MinPly));
        if( gameState == GameState.WAIT ){
            if( seatCnt > minPly-1 ){
                hasNext = true;
            }
        }else{
            if( seatCnt > minPly-1 ){
                gameState = GameState.WAIT;
                hasNext = false;
            }
        }
        return hasNext;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(GameTick.class, c -> {
                    if( (tickCnt%100)==0)
                        log.info(String.format("Game Tick:%d",gameid));

                    if(isStartGame() && tickCnt %10==0 ){
                        gameCore.tell(new GameStateInfo(GameState.START),ActorRef.noSender());
                        gameState=GameState.START;
                    }
                    tickCnt++;
                    if(tickCnt>Integer.MAX_VALUE-1000){
                        tickCnt=0;
                        log.debug("Tick Reset");
                    }
                })
                .match(Greeting.class, c -> {
                    String name = c.getName();
                    getSender().tell("Hello, " + name ,getSelf()); // response for test
                })
                .match(JoinPly.class, j->{
                    connectPly(j.getPly());
                })
                .match(SeatIn.class, s->{
                    seatPly(s.getPlayer());
                })
                .match(SeatOut.class, s->{
                    seatOutPly(s.getPlayer());
                })
                .match(GameStateInfo.class,g->{
                    gameState = g.getGameState();
                })
                .match(ActionMessage.class, a->{
                    ActionMessage.Cmd cmd = a.getCmd();
                    if(cmd== ActionMessage.Cmd.ADD){
                        addGameMessage(a.getSessionMessage());
                    }else if(cmd== ActionMessage.Cmd.PEEK){
                        SessionMessage sessionMessage = peekGameMessage();
                        if(sessionMessage!=null)
                            getSender().tell(sessionMessage.gameMessage,ActorRef.noSender());
                    }else if(cmd== ActionMessage.Cmd.CLEAR){
                        actionMessage.clear();
                    }
                })
                .build();
    }
}
