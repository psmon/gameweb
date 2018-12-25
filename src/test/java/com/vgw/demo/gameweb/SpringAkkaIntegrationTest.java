package com.vgw.demo.gameweb;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import com.vgw.demo.gameweb.config.AppConfiguration;
import com.vgw.demo.gameweb.message.Greeting;
import com.vgw.demo.gameweb.message.actor.TableInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static com.vgw.demo.gameweb.config.SpringExtension.SPRING_EXTENSION_PROVIDER;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfiguration.class)
@SuppressWarnings("Duplicates")
public class SpringAkkaIntegrationTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ActorSystem system;

    @Test
    public void whenCallingGreetingActor_thenActorGreetsTheCaller() throws Exception {
        ActorRef greeter = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("greetingActor"), "greeter");

        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> result = ask(greeter, new Greeting("John"), timeout);
        Assert.assertEquals("Hello, John", Await.result(result, duration));
    }

    @Test
    public void createLobbyAndTable() throws Exception{
        // Create LobbyActor
        ActorRef lobby = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("lobbyActor"), "testlobby");
        // Create TableActor
        for(int i=0;i<10;i++){
            lobby.tell( new TableInfo(i,"table-"+i ,TableInfo.TableCmd.CREATE),ActorRef.noSender() );
        }
    }

    @Test
    public void createTableinExistLobby() throws Exception{
        ActorSelection lobby = system.actorSelection("/user/lobby");
        // Create TableActor
        for(int i=10;i<20;i++){
            lobby.tell( new TableInfo(i,"table-"+i ,TableInfo.TableCmd.CREATE),ActorRef.noSender() );
        }
        // Response Test for TableActor
        ActorSelection someTable = system.actorSelection("/user/lobby/table-10");
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> result = ask(someTable, new Greeting("John"), timeout);
        Assert.assertEquals("Hello, John", Await.result(result, duration));
    }

    @Test
    public void createTableAndPingGame() throws Exception{
        // Select Lobby
        ActorSelection lobby = system.actorSelection("/user/lobby");

        // Create Unique Table( 1000 )
        lobby.tell( new TableInfo(1000,"table-"+1000 ,TableInfo.TableCmd.CREATE),ActorRef.noSender() );

        // AutoCreate Game by TableActor
        // Just Game Select
        ActorSelection game = system.actorSelection("/user/lobby/table-1000/game-1000");

        //Test Game Response
        FiniteDuration duration = FiniteDuration.create(1, TimeUnit.SECONDS);
        Timeout timeout = Timeout.durationToTimeout(duration);
        Future<Object> result = ask(game, new Greeting("Game"), timeout);
        Assert.assertEquals("Hello, Game", Await.result(result, duration));
    }

    @After
    public void tearDown() {
        system.terminate();
    }

}
