
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import actor.match.MatchProtocol.Command;
import actor.match.MatchProtocol.Incident;
import actor.match.MatchProtocol.Update;
import actor.match.MatchProtocol.View;
import actor.match.MatchSupervisor;
import akka.actor.testkit.typed.Effect;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import java.util.Optional;
import org.junit.Test;

public class MatchSupervisorTest {

    @Test
    public void spawnMatchActors() {
        // When a new Match is created, it should spawn a new Actor
        BehaviorTestKit<Command> test = BehaviorTestKit.create(MatchSupervisor.create());

        Update matchCommand = Update
                .of(Incident.of("test-match", 1, null, null, null), Optional.empty());
        test.run(matchCommand);
        TestInbox<Update> messageToMatch = test.childInbox("test-match");
        assertEquals("test-match", test.expectEffectClass(Effect.Spawned.class).childName());
        messageToMatch.expectMessage(Update.of(matchCommand.getIncident(), Optional.of(test.getRef())));

        // No effects when sent again.
        test.run(matchCommand);
        assertFalse(test.hasEffects());
        messageToMatch.expectMessage(Update.of(matchCommand.getIncident(), Optional.of(test.getRef())));
    }

    @Test
    public void forwardViewMessageToAllActors() {

        // Start of creation
        BehaviorTestKit<Command> test = BehaviorTestKit.create(MatchSupervisor.create());
        Update matchCommand = Update
                .of(Incident.of("test-match", 1, null, null, null), Optional.empty());
        test.run(matchCommand);
        // End of creation

        test.run(View.of());

        TestInbox<Command> childInbox = test.childInbox("test-match");

        childInbox.expectMessage(Update.of(matchCommand.getIncident(), Optional.of(test.getRef()))); // creation
        childInbox.expectMessage(View.of()); // creation
    }
}
