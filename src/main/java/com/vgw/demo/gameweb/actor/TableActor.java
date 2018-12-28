package com.vgw.demo.gameweb.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.fakegame.Player;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import com.vgw.demo.gameweb.message.actor.GameTick;
import com.vgw.demo.gameweb.message.actor.JoinPlyReq;
import com.vgw.demo.gameweb.message.actor.TableCreateReq;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class TableActor extends AbstractActor {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef lobbyActor;
    private ActorRef gameActor;
    private Cancellable gameCancellable;

    private int tableId;
    private String name;
    private int maxPly;
    private int minPly;
    private int dealer;

    List<Player> playList;
    List<Player>    viewList;
    ArrayList<Boolean> avableSeat;

    private ActorSystem system;

    // Props == Object creation hint
    static public Props props(TableCreateReq tableCreateReq, ActorRef lobbyActor) {
        return Props.create(TableActor.class, () -> new TableActor(tableCreateReq, lobbyActor));
    }

    public TableActor(TableCreateReq tableCreateReq, ActorRef lobbyActor){
        this.tableId= tableCreateReq.getTableId();
        this.lobbyActor=lobbyActor;
        system = getContext().getSystem();
    }


    public Player findUser(String session,Boolean isView){
        Player result=null;
        List<Player> userList = isView ==true ? viewList : playList;

        for(Player ply:userList){
            if(ply.getSession().equals(session))
                result = ply;
        }
        return result;
    }

    public Player findUser(int setNo){
        Player result=null;
        List<Player> userList = playList;

        for(Player ply:userList){
            if(ply.getSeatNo()==setNo)
                result = ply;
        }
        return result;
    }

    protected void updatePly(Player plyayer,Boolean isView){
        List<Player> userList = isView ==true ? viewList : playList;
        for(Player ply:userList){
            if(ply.getSession().equals(plyayer.getSession())){
                ply=plyayer;
                break;
            }
        }
    }

    protected void deletePly(Player plyayer){
        avableSeat.set(plyayer.getSeatNo(),true);
        playList.remove(plyayer);
    }

    protected void deleteViewPly(Player plyayer){
        viewList.remove(plyayer);
    }

    protected void addUser(Player ply){
        playList.add(ply);
    }

    protected void joinTable(Player ply){
        if(findUser(ply.getSession(),true)==null){
            viewList.add(ply);
            //game.OnConnectPly(ply);
        }else{
            //game.OnError(ply,"duplicate connect");
        }
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ConnectInfo.class, c -> {
                })
                .match(Greeting.class, c -> {
                    String name = c.getName();
                    getSender().tell("Hello, " + name ,getSelf()); // response for test
                })
                .match(TableCreateReq.class, t->{
                    if(t.getCmd()== TableCreateReq.TableCmd.CREATE){
                        String gameUID = "game-" + tableId;
                        gameActor = getContext().actorOf( GameActor.props(t,getSelf()), gameUID);
                        log.info(String.format("Create Table:%d",t.getTableId()));

                        gameCancellable = system.scheduler().schedule(Duration.ZERO,
                                Duration.ofMillis(100), gameActor, new GameTick(),
                                system.dispatcher(), null);
                    }else if(t.getCmd() == TableCreateReq.TableCmd.DELETE){
                        //TODO: Just Cancel or Ask for Stop game
                        gameCancellable.cancel();
                    }
                })
                .match(JoinPlyReq.class, j->{

                })
                .build();
    }

}

