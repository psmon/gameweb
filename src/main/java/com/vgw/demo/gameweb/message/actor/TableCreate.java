package com.vgw.demo.gameweb.message.actor;

public class TableCreate {

    //TODO : REMOVE
    public enum Cmd
    {
        CREATE,DELETE,INFO
    }

    private int tableId;
    private String name;
    private Cmd cmd;
    private int playCnt;
    private int viewCnt;

    public TableCreate(int tableId, String name, Cmd cmd) {
        this.tableId = tableId;
        this.name = name;
        this.cmd = cmd;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }

    public int getPlayCnt() {
        return playCnt;
    }

    public void setPlayCnt(int playCnt) {
        this.playCnt = playCnt;
    }

    public int getViewCnt() {
        return viewCnt;
    }

    public void setViewCnt(int viewCnt) {
        this.viewCnt = viewCnt;
    }
}
