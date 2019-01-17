package com.vgw.demo.gameweb.feign;

import com.vgw.demo.gameweb.controler.rest.client.GameInfoClient;
import com.vgw.demo.gameweb.controler.rest.client.GameInfoClientBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


//For Live Test
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GameInfoTest {
    private GameInfoClient gameInfoClient;

    @Before
    public void setup() {
        GameInfoClientBuilder feignClientBuilder = new GameInfoClientBuilder();
        gameInfoClient = feignClientBuilder.getGameInfoClient();
    }

    @Test
    public void givenGameClient_shouldFindDealer() throws Exception {
        int dealer = gameInfoClient.findDealerFromGame("1");
        Assert.assertTrue(-100 < dealer);
    }


    @Test
    public void givenGameClient_shouldPingPong() throws Exception {
        String pong = gameInfoClient.pingGame("1");
        Assert.assertEquals("ok",pong);
    }

}
