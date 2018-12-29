package com.vgw.demo.gameweb.message.actor;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class ConnectInfo {

    public enum Cmd
    {
        CONNECT,DISCONET
    }

    private String sessionId;
    private SimpMessageSendingOperations    wsSender;
    private Cmd cmd;

    public ConnectInfo(String sessionId, SimpMessageSendingOperations wsSender, Cmd cmd) {
        this.sessionId = sessionId;
        this.wsSender = wsSender;
        this.cmd = cmd;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public SimpMessageSendingOperations getWsSender() {
        return wsSender;
    }

    public void setWsSender(SimpMessageSendingOperations wsSender) {
        this.wsSender = wsSender;
    }

    public Cmd getCmd() {
        return cmd;
    }

    public void setCmd(Cmd cmd) {
        this.cmd = cmd;
    }
}
