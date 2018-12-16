package com.vgw.demo.gameweb.fakegame;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private int tableId;
    private int maxPly;
    private int minPly;
    private int dealer;

    List<Player>    playList;
    List<Player>    viewList;
    private Game    game;

    protected void initTable(){
        maxPly=9;
        minPly=3;
        playList = new ArrayList<>();
        viewList = new ArrayList<>();
    }

    public  Table(){
        initTable();
        //game = new Game(this);
    }

    // Todo : Name--> Unique ID Base...
    public Player findUser(String name){
        Player result=null;
        for(Player ply:playList){
            if(ply.getName().equals(name))
                result = ply;
        }
        return result;
    }

    protected void updatePly(Player plyayer){
        for(Player ply:playList){
            if(ply.getName().equals(plyayer.getName())){
                ply=plyayer;
                break;
            }
        }
    }

    protected void deletePly(Player plyayer){
        playList.remove(plyayer);
    }

    protected void addUser(Player ply){
        playList.add(ply);
    }

    public void joinTable(Player ply){
        //Todo: dupilcated check by session
        viewList.add(ply);
        game.OnConnectPly(ply);
    }

    public void seatUser(Player ply){
        if(findUser(ply.getName())==null){
            if(playList.size()<maxPly){
                addUser(ply);
            }
        }else{
            //Reconnect
            updatePly(ply);
        }
    }

    public void leaveUser(Player ply){
        if(findUser(ply.getName())==null){
            //where aru you?
        }else{
            deletePly(ply);
        }
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
