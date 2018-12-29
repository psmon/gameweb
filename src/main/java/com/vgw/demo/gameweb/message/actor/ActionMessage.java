package com.vgw.demo.gameweb.message.actor;

public class ActionMessage {

    public enum Cmd
    {
        ADD,PEEK,CLEAR
    }

    private Cmd cmd;

    public ActionMessage(Cmd cmd) {
        this.cmd = cmd;
    }

    public Cmd getCmd() {
        return cmd;
    }
}
