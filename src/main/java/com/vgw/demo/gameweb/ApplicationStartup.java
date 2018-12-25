package com.vgw.demo.gameweb;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.vgw.demo.gameweb.message.actor.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.vgw.demo.gameweb.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);


    @Autowired
    private ActorSystem system;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        logger.info("Create LobbyActor - name:user/lobby ");
        // Create LobbyActor
        ActorRef lobby = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("lobbyActor"), "lobby");
        // Create TableActor
        for(int i=0;i<10;i++){
            lobby.tell( new TableInfo(i,"table-"+i ,TableInfo.TableCmd.CREATE),ActorRef.noSender() );
        }
    }

}