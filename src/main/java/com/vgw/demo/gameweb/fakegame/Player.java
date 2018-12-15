package com.vgw.demo.gameweb.fakegame;

import java.security.Principal;

public class Player {
    private String  session;
    private String id;
    private String name;
    private int totalMoney;
    private int chips;
    private int card;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void updateChups(int amount){
        chips+=amount;
    }
    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }
}
