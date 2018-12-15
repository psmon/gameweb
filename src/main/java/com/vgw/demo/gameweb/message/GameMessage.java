package com.vgw.demo.gameweb.message;

public class GameMessage {
    private MessageType type;
    private String content;
    private String sender;
    private Object gameinfo;

    public enum MessageType {
        CHAT,
        VIEW,
        JOIN,
        GAME,
        LEAVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Object getGameinfo() {
        return gameinfo;
    }

    public void setGameinfo(Object gameinfo) {
        this.gameinfo = gameinfo;
    }
}