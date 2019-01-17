package com.vgw.demo.gameweb.controler.rest.client;

import feign.Feign;
import feign.Logger;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public class GameInfoClientBuilder {
    private GameInfoClient gameInfoClient = createClient(GameInfoClient.class, "http://localhost:8080/gameinfo");

    private static <T> T createClient(Class<T> type, String uri) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new Slf4jLogger(type))
                .logLevel(Logger.Level.FULL)
                .target(type, uri);
    }

    public GameInfoClient getGameInfoClient() {
        return gameInfoClient;
    }

    public void setGameInfoClient(GameInfoClient gameInfoClient) {
        this.gameInfoClient = gameInfoClient;
    }
}
