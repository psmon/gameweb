package com.vgw.demo.gameweb.fakegame;

public class Table {
    private int tableId;
    private int maxPly;
    private int minPly;

    private Game    game;

    public  Table(){
        maxPly=9;
        minPly=3;
        //game = new Game(this);
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
