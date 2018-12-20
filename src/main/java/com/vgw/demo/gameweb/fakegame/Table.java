package com.vgw.demo.gameweb.fakegame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private static final Logger logger = LoggerFactory.getLogger(Table.class);

    private int tableId;
    private String name;
    private int maxPly;
    private int minPly;
    private int dealer;

    List<Player>    playList;
    List<Player>    viewList;
    ArrayList<Boolean>  avableSeat;

    private Game    game;

    protected void initTable(){
        maxPly=7;
        minPly=3;
        playList = new ArrayList<>();
        viewList = new ArrayList<>();
        avableSeat = new ArrayList<>();
        for(int idx=0;idx<maxPly;idx++){
            avableSeat.add(true);
        }
    }

    public  Table(){
        initTable();
        //game = new Game(this);
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

    public void joinTable(Player ply){
        if(findUser(ply.getSession(),true)==null){
            viewList.add(ply);
            game.OnConnectPly(ply);
        }else{
            game.OnError(ply,"duplicate connect");
        }
    }

    protected int getAvableSeatAnyAndReseved(){
        int searchIdx=-1;
        for(int idx=0;idx<maxPly;idx++){
            if(avableSeat.get(idx)==true){
                avableSeat.set(idx,false);
                searchIdx=idx;
                break;
            }
        }
        return searchIdx;
    }

    public void cleanUser(String session){
        Player ply = findUser(session,false);
        if(ply!=null){
            leaveSearUser(ply);
        }
        Player view = findUser(session,true);
        leaveUser(view);
    }

    public void seatOutUser(Player ply){
        game.OnSeatOutPly(ply);
        playList.remove(ply);
    }

    public void seatUser(Player ply){
        if(findUser(ply.getSession(),false)==null){
            if(playList.size()<maxPly){
                int revSeatNo = getAvableSeatAnyAndReseved();
                if(revSeatNo>-1){
                    //Auto Seat
                    ply.setSeatNo(revSeatNo);
                    addUser(ply);
                    game.OnSeatPly(ply);
                }
            }
        }else{
            //Reconnect
            updatePly(ply,false);
        }
    }

    public void leaveSearUser(Player ply){
        seatOutUser(ply);
        deletePly(ply);
    }

    public void leaveUser(Player ply){
        deleteViewPly(ply);
    }

    public int getSeatCnt(){
        return playList.size();
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
        game = new Game();
        game.setTable(this);
        game.start();
    }

    public int getMaxPly() {
        return maxPly;
    }

    public void setMaxPly(int maxPly) {
        this.maxPly = maxPly;
    }

    public int getMinPly() {
        return minPly;
    }

    public void setMinPly(int minPly) {
        this.minPly = minPly;
    }

    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    public void setDealerNext(){
        int plySize = playList.size();
    }

    public List<Player> getPlayList() {
        return playList;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
