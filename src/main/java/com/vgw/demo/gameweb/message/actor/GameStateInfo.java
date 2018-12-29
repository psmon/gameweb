package com.vgw.demo.gameweb.message.actor;

import com.vgw.demo.gameweb.actor.GameActor;

public class GameStateInfo {

    private GameActor.GameState gameState;

    public GameStateInfo(GameActor.GameState gameState) {
        this.gameState = gameState;
    }

    public GameActor.GameState getGameState() {
        return gameState;
    }
}
