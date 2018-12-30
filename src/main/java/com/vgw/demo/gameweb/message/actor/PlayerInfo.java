package com.vgw.demo.gameweb.message.actor;

public class PlayerInfo {

    private String sessionId;
    private Integer seatNo;

    public PlayerInfo(String sessionId) {
        this.sessionId = sessionId;
    }

    public PlayerInfo(Integer seatNo) {
        this.seatNo = seatNo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getSeatNo() {
        return seatNo;
    }
}
