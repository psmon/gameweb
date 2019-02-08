package com.vgw.demo.gameweb.actor;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;
import akka.testkit.javadsl.TestKit;
import com.vgw.demo.gameweb.config.AppConfiguration;
import com.vgw.demo.gameweb.message.actor.TableCreate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;

@SpringBootTest
@ContextConfiguration(classes = AppConfiguration.class)
public class RouterActorTest {

    //Actors can be remotely distributed and have a routing strategy.
    //The routing function is granted and the actor itself is not modified.

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void roundRobbinTest(){
        new TestKit(system) {{
            final ActorRef observer = getRef();

            ActorRef lobbyActors = system.actorOf(LobbyActor.props().
                    withRouter(new RoundRobinPool(8)));

            lobbyActors.tell("ping",observer);
            expectMsg(Duration.ofSeconds(1),"pong");
            lobbyActors.tell("ping",observer);
            expectMsg(Duration.ofSeconds(1),"pong");
            lobbyActors.tell("ping",observer);
            expectMsg(Duration.ofSeconds(1),"pong");
            lobbyActors.tell("ping",observer);
            expectMsg(Duration.ofSeconds(1),"pong");
            lobbyActors.tell("ping",observer);
        }};
    }
}
