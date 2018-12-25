package com.vgw.demo.gameweb.message.actor;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class ConnectInfo {

    public enum ConnectCmd
    {
        CONNECT,DISCONET
    };

    private String sessionId;
    private SimpMessageSendingOperations    wsSender;
    private ConnectCmd connectCmd;

    public ConnectInfo(String sessionId, SimpMessageSendingOperations wsSender, ConnectCmd connectCmd) {
        this.sessionId = sessionId;
        this.wsSender = wsSender;
        this.connectCmd = connectCmd;
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

    public ConnectCmd getConnectCmd() {
        return connectCmd;
    }

    public void setConnectCmd(ConnectCmd connectCmd) {
        this.connectCmd = connectCmd;
    }
}
