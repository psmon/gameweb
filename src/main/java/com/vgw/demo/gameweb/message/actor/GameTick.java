package com.vgw.demo.gameweb.message.actor;

public class GameTick {

    public enum Cmd
    {
        PING,GAMEPING,TESTPING
    }

    protected Cmd cmd;

    public GameTick() {
        cmd = Cmd.PING;
    }
    public GameTick(Cmd cmd) {
        this.cmd = cmd;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }
}
