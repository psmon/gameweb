package com.vgw.demo.gameweb.message.actor;

import com.vgw.demo.gameweb.message.GameMessage;

public class MessageWS {
    private String session;
    private GameMessage gameMessage;

    public MessageWS(String session, GameMessage gameMessage) {
        this.session = session;
        this.gameMessage = gameMessage;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public GameMessage getGameMessage() {
        return gameMessage;
    }

    public void setGameMessage(GameMessage gameMessage) {
        this.gameMessage = gameMessage;
    }
}
