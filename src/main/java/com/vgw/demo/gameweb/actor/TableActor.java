package com.vgw.demo.gameweb.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.gameobj.Player;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("Duplicates")
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

    private List<Player> playList;
    private List<Player> viewList;
    private ArrayList<Boolean> avableSeat;

    private ActorSystem system;

    // Props == Object creation hint
    static public Props props(TableCreate tableCreate, ActorRef lobbyActor) {
        return Props.create(TableActor.class, () -> new TableActor(tableCreate, lobbyActor));
    }

    TableActor(TableCreate tableCreate, ActorRef lobbyActor){
        this.tableId= tableCreate.getTableId();
        this.lobbyActor=lobbyActor;
        initTable();
        system = getContext().getSystem();
    }

    private void initTable(){
        maxPly=7;
        minPly=3;
        dealer=-1;
        playList = new ArrayList<>();
        viewList = new ArrayList<>();
        avableSeat = new ArrayList<>();
        for(int idx=0;idx<maxPly;idx++){
            avableSeat.add(true);
        }
    }

    private Player findUser(String session,Boolean isView){
        Player result=null;
        List<Player> userList = isView ? viewList : playList;

        for(Player ply:userList){
            if(ply.getSession().equals(session))
                result = ply;
        }
        return result;
    }

    protected Player findUser(int setNo){
        Player result=null;
        List<Player> userList = playList;

        for(Player ply:userList){
            if(ply.getSeatNo()==setNo)
                result = ply;
        }
        return result;
    }

    private void updatePly(Player plyayer,Boolean isView){
        List<Player> userList = isView ? viewList : playList;
        for(Player ply:userList){
            if(ply.getSession().equals(plyayer.getSession())){
                ply=plyayer;
                break;
            }
        }
    }

    private void deletePly(Player plyayer){
        avableSeat.set(plyayer.getSeatNo(),true);
        playList.remove(plyayer);
    }

    private void deleteViewPly(Player plyayer){
        viewList.remove(plyayer);
    }

    private void addUser(Player ply){
        playList.add(ply);
    }

    protected void joinTable(Player ply){
        if(findUser(ply.getSession(),true)==null){
            viewList.add(ply);
            gameActor.tell(new JoinPly(ply),getSender());
        }else{
            //game.OnError(ply,"duplicate connect");
        }
    }

    private int getAvableSeatAnyAndReseved(){
        int searchIdx=-1;
        for(int idx=0;idx<maxPly;idx++){
            if(avableSeat.get(idx)){
                avableSeat.set(idx,false);
                searchIdx=idx;
                break;
            }
        }
        return searchIdx;
    }

    protected void cleanUser(String session){
        Player ply = findUser(session,false);
        if(ply!=null){
            leaveSeatUser(ply);
        }
        Player view = findUser(session,true);
        leaveUser(view);
    }

    private void seatOutUser(Player ply){
        gameActor.tell( new SeatOut(ply),ActorRef.noSender() );
        playList.remove(ply);
    }

    protected void seatUser(Player ply){
        if(findUser(ply.getSession(),false)==null){
            if(playList.size()<maxPly){
                int revSeatNo = getAvableSeatAnyAndReseved();
                if(revSeatNo>-1){
                    //Auto Seat
                    ply.setSeatNo(revSeatNo);
                    addUser(ply);
                    gameActor.tell(new SeatIn(ply),ActorRef.noSender());
                }
            }
        }else{
            //Reconnect
            updatePly(ply,false);
        }
    }

    private void leaveSeatUser(Player ply){
        seatOutUser(ply);
        deletePly(ply);
    }

    private void leaveUser(Player ply){
        deleteViewPly(ply);
    }

    protected int getSeatCnt(){
        return playList.size();
    }

    protected void setNextDealer(){
        int nextDealer=-1;
        List<Player> sortList = new ArrayList<>(playList);
        Collections.sort(sortList, (a, b) -> a.getSeatNo() < b.getSeatNo() ? -1 : a.getSeatNo() == b.getSeatNo() ? 0 : 1);

        boolean findDealer = false;
        Player firstPly=sortList.get(0);
        for(Player ply:sortList){
            if(findDealer){
                nextDealer=ply.getSeatNo();
                break;
            }
            if(dealer == ply.getSeatNo()){
                findDealer=true;
            }
        }

        if(nextDealer==-1){
            nextDealer=firstPly.getSeatNo();
        }

        dealer=nextDealer;
    }

    private List<Player> getPlayList(boolean isDealerOrder) {
        List<Player> listOrder;
        if(isDealerOrder){
            List<Player> sortList = new ArrayList<>(playList);
            List<Player> sortDealerList = new ArrayList<>();
            List<Player> addLast = new ArrayList<>();
            Collections.sort(sortList, (a, b) -> a.getSeatNo() < b.getSeatNo() ? -1 : a.getSeatNo() == b.getSeatNo() ? 0 : 1);
            for(Player ply:sortList){
                if(dealer<= ply.getSeatNo()){
                    sortDealerList.add(ply);
                }else{
                    addLast.add(ply);
                }
            }
            sortDealerList.addAll(addLast);
            listOrder=sortDealerList;
        }else{
            listOrder=playList;
        }
        return listOrder;
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
                .match(TableCreate.class, t->{
                    if(t.getCmd()== TableCreate.Cmd.CREATE){
                        String gameUID = "game-" + tableId;
                        gameActor = getContext().actorOf( GameActor.props(t,getSelf()), gameUID);
                        log.info(String.format("Create Table:%d",t.getTableId()));

                        gameCancellable = system.scheduler().schedule(Duration.ZERO,
                                Duration.ofMillis(100), gameActor, new GameTick(),
                                system.dispatcher(), null);
                    }else if(t.getCmd() == TableCreate.Cmd.DELETE){
                        //TODO: Just Cancel or Ask for Stop game
                        gameCancellable.cancel();
                    }
                })
                .match(JoinPly.class, j->{
                    joinTable(j.getPly());
                })
                .match(SeatIn.class, s->{
                    seatUser(s.getPlayer());
                })
                .match(SeatOut.class,s->{
                    cleanUser(s.getPlayer().getSession());
                })
                .match(TableInfo.class, t->{
                    TableInfo.Cmd cmd = t.getCmd();
                    if(cmd==TableInfo.Cmd.SeatCnt){
                        getSender().tell(playList.size(),ActorRef.noSender());
                    }else if(cmd==TableInfo.Cmd.DealerNext){
                        setNextDealer();
                        getSender().tell(dealer,ActorRef.noSender());
                    }else if(cmd==TableInfo.Cmd.MinPly){
                        getSender().tell(minPly,ActorRef.noSender());
                    }else if(cmd==TableInfo.Cmd.ViewCnt){
                        getSender().tell(viewList.size(),ActorRef.noSender());
                    }else if(cmd==TableInfo.Cmd.DealerPos){
                        getSender().tell(dealer,ActorRef.noSender());
                    }
                })
                .match(PlayerList.class, p->{
                    if(p.getCmd() == PlayerList.Cmd.PLAYER){
                        getSender().tell(getPlayList(false),ActorRef.noSender());
                    }else if(p.getCmd() == PlayerList.Cmd.PLAYER_DEALER_ORDER){
                        getSender().tell(getPlayList(true),ActorRef.noSender());
                    }else if(p.getCmd() == PlayerList.Cmd.ALL){
                        getSender().tell(viewList,ActorRef.noSender());
                    }
                })
                .match(PlayerInfo.class,p->{
                    Player user = findUser(p.getSeatNo());
                    getSender().tell(user,ActorRef.noSender());
                })
                .build();
    }

}

