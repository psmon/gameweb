package com.vgw.demo.gameweb.controler;

import com.vgw.demo.gameweb.fakegame.Lobby;
import com.vgw.demo.gameweb.message.GameMessage;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.HelloMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    // Just Ping check

    @Autowired
    Lobby lobby;

    @MessageMapping("/hello")
    @SendTo("/topic/public")
    public GameMessage greeting(GameMessage message) throws Exception {
        return lobby.sayHello(message);
    }

}
