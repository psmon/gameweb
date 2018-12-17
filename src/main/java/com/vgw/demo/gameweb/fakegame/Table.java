package com.vgw.demo.gameweb.fakegame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {

    private static final Logger logger = LoggerFactory.getLogger(Table.class);

    private int tableId;
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
            if(ply.getName().equals(plyayer.getName())){
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

    public void seatUser(Player ply){
        if(findUser(ply.getSession(),false)==null){
            if(playList.size()<maxPly){
                int revSeatNo = getAvableSeatAnyAndReseved();
                if(revSeatNo>-1){
                    ply.setSeatNo(revSeatNo);
                    addUser(ply);
                }
            }
        }else{
            //Reconnect
            updatePly(ply,false);
        }
    }

    public void leaveUser(Player ply){
        deletePly(ply);
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
