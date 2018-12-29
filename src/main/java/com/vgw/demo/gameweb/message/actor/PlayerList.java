package com.vgw.demo.gameweb.message.actor;

public class PlayerList {

    public enum Cmd
    {
        ALL,PLAYER, PLAYER_DEALER_ORDER
    }

    private Cmd cmd;

    public PlayerList(Cmd cmd) {
        this.cmd = cmd;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }
}
