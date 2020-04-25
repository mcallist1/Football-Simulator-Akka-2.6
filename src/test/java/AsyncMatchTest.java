import actor.match.Match;
import actor.match.MatchProtocol.Command;
import actor.match.MatchProtocol.Confirmation;
import actor.match.MatchProtocol.Incident;
import actor.match.MatchProtocol.Update;
import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import java.util.Optional;
import org.junit.AfterClass;
import org.junit.Test;

public class AsyncMatchTest {

    static final ActorTestKit testKit = ActorTestKit.create();

    @AfterClass
    public static void cleanup() {
        testKit.shutdownTestKit();
    }

    @Test
    public void testConfirmation() {
        ActorRef<Command> matchActor = testKit.spawn(Match.create("test-match"));
        TestProbe<Command> probe = testKit.createTestProbe();
        matchActor.tell(Update.of(Incident.of("", 0, null, null, null), Optional.of(probe.getRef())));
        probe.expectMessage(Confirmation.of("test-match"));
    }
}
