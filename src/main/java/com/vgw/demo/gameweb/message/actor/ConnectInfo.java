package com.vgw.demo.gameweb.message.actor;

import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class ConnectInfo {

    public enum ConnectState
    {
        CONNECT,DISCONET
    };

    private String sessionId;
    private SimpMessageSendingOperations    wsSender;
    private ConnectState connectState;

    public ConnectInfo(String sessionId, SimpMessageSendingOperations wsSender, ConnectState connectState) {
        this.sessionId = sessionId;
        this.wsSender = wsSender;
        this.connectState = connectState;
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

    public ConnectState getConnectState() {
        return connectState;
    }

    public void setConnectState(ConnectState connectState) {
        this.connectState = connectState;
    }
}
