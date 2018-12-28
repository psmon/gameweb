package com.vgw.demo.gameweb.message.actor;

public class TableCreateReq {

    //TODO : REMOVE
    public enum TableCmd
    {
        CREATE,DELETE,INFO
    };

    private int tableId;
    private String name;
    private TableCmd cmd;
    private int playCnt;
    private int viewCnt;

    public TableCreateReq(int tableId, String name, TableCmd cmd) {
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

    public TableCmd getCmd() {
        return cmd;
    }

    public void setCmd(TableCmd cmd) {
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
