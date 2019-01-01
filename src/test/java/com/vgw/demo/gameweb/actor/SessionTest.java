package com.vgw.demo.gameweb.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.vgw.demo.gameweb.config.AppConfiguration;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import com.vgw.demo.gameweb.message.actor.JoinGame;
import com.vgw.demo.gameweb.message.actor.TableCreate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfiguration.class)
@SuppressWarnings("Duplicates")
public class SessionTest {

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
    public void testIt() {
        new TestKit(system) {{
            final ActorRef lobbyActor = system.actorOf(LobbyActor.props(),"lobby");
            final String testSessionID = "jaskfjkjaslfalsf";

            // Create TableActor
            for(int i=0;i<10;i++){
                lobbyActor.tell( new TableCreate(i,"table-"+i , TableCreate.Cmd.CREATE),getRef() );
                expectMsg(Duration.ofSeconds(1), "created");
            }

            // Try Connect
            lobbyActor.tell(new ConnectInfo(testSessionID, null,ConnectInfo.Cmd.CONNECT),getRef());
            expectMsg(Duration.ofSeconds(1), "done");

            // Find User
            lobbyActor.tell(new ConnectInfo(testSessionID, null,ConnectInfo.Cmd.FIND),getRef());
            expectMsg(Duration.ofSeconds(1), "User exists");

            // Join Table : Forward Check , lobby->table->game->getRef()
            lobbyActor.tell(new JoinGame(1,"test",testSessionID),getRef() );
            expectMsg(Duration.ofSeconds(1), "joined");

            // Try Disconnect
            lobbyActor.tell(new ConnectInfo(testSessionID, null,ConnectInfo.Cmd.DISCONET),getRef());
            expectMsg(Duration.ofSeconds(3), "done");

            // Find Again
            lobbyActor.tell(new ConnectInfo(testSessionID, null,ConnectInfo.Cmd.FIND),getRef());
            expectMsg(Duration.ofSeconds(1), "User does not exist");

        }};
    }


}
