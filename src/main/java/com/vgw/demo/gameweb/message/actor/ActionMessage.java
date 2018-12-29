package com.vgw.demo.gameweb.message.actor;

import com.vgw.demo.gameweb.message.SessionMessage;

public class ActionMessage {

    public enum Cmd
    {
        ADD,PEEK,CLEAR
    }

    private Cmd cmd;

    private SessionMessage sessionMessage;

    public ActionMessage(Cmd cmd) {
        this.cmd = cmd;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public SessionMessage getSessionMessage() {
        return sessionMessage;
    }

    public void setSessionMessage(SessionMessage sessionMessage) {
        this.sessionMessage = sessionMessage;
    }
}
