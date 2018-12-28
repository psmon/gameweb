package com.vgw.demo.gameweb.message.actor;

public class JoinGameReq {
    private int tableId;
    private String name;
    private String session;

    public JoinGameReq(int tableId, String name, String session) {
        this.tableId = tableId;
        this.name = name;
        this.session = session;
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

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
