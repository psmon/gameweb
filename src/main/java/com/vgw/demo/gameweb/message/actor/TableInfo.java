package com.vgw.demo.gameweb.message.actor;

public class TableInfo {

    public enum Cmd
    {
        DealerNext,DealerPos,SeatCnt,ViewCnt,MinPly,MaxPly
    }

    private Cmd cmd;

    public TableInfo(Cmd cmd) {
        this.cmd = cmd;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }
}
