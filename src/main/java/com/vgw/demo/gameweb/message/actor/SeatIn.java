package com.vgw.demo.gameweb.message.actor;

import com.vgw.demo.gameweb.gameobj.Player;

public class SeatIn {
    private Player player;

    public SeatIn(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
