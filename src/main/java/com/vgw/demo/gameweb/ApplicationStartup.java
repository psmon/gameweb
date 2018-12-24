package com.vgw.demo.gameweb;

import akka.actor.ActorSystem;
import com.vgw.demo.gameweb.config.SpringExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);


    @Autowired
    private ActorSystem system;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        logger.info("Create LobbyActor - name:user/lobby ");
        system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props("lobbyActor"), "lobby");
    }

}