package com.vgw.demo.gameweb.message;

public class GameMessage {
    private MessageType type;
    private String content;
    private String sender;
    private Object info;

    public enum MessageType {
        NONE,
        ERROR,
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

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }
}