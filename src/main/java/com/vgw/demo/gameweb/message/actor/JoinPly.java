package com.vgw.demo.gameweb.message.actor;

import com.vgw.demo.gameweb.fakegame.Player;

public class JoinPly {

    private Player ply;

    public JoinPly(Player ply) {
        this.ply = ply;
    }

    public Player getPly() {
        return ply;
    }

    public void setPly(Player ply) {
        this.ply = ply;
    }
}
