# UnitTest TOOLS


# AKKA TestToolkit
Testkit allows you to test your actors in a controlled but realistic environment. The definition of the environment depends very much on the problem at hand and the level at which you intend to test, ranging from simple checks to full system tests.

test code:
- SpringAkkaIntergrationTest.java
- actor/SessionTest.java
- actor/EventSourcingTest.java - preparing for CQRS : 

link:
- https://www.baeldung.com/akka-with-spring
- https://doc.akka.io/docs/akka/2.5/testing.html



## UnitTest Sample

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
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


# FeignClient
Feign is a convenient way to test your application’s API, focused on creating tests to verify business logic instead of spending time on the technical implementation of web services clients.

test code:
- feign/GameInfoTest.java

link:
- https://www.blazemeter.com/blog/Rest-API-testing-with-Spring-Cloud-Feign-Clients
- https://github.com/velo/feign-mock


## UnitTest Sample
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


#### Todo:Next
UnitTest for CQRS - 

- https://github.com/Romeh/spring-boot-akka-event-sourcing-starter
- https://github.com/SBozhko/akka-examples/blob/master/Chapter8/AkkaUnitTest/src/test/java/org/akka/essentials/unittest/example/ExampleUnitTest.java



