package com.vgw.demo.gameweb.controler.rest.client;

// Note : Actually, it should be implemented in a separate location from the server.
import feign.Param;
import feign.RequestLine;

import java.awt.print.Book;

public interface GameInfoClient {
    @RequestLine("GET /dealer/{gameId}")
    int findDealerFromGame(@Param("gameId") String gameId);

    @RequestLine("GET /ping/{gameId}")
    String pingGame(@Param("gameId") String gameId);
}
