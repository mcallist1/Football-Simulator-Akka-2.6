
import static org.junit.Assert.assertThat;

import actor.match.Match;
import actor.match.MatchProtocol.Command;
import actor.match.MatchProtocol.Incident;
import actor.match.MatchProtocol.Update;
import actor.match.MatchProtocol.View;
import akka.actor.testkit.typed.CapturedLogEvent;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.slf4j.event.Level;

public class MatchTest {

    @Test
    public void testUpdate() {
        BehaviorTestKit<Command> test = BehaviorTestKit.create(Match.create("test-match"));

        Incident incident = Incident.of("test-match", 1, null, null, null);
        Update matchCommand = Update.of(incident, Optional.empty());
        test.run(matchCommand);
        View viewCommand = View.of();
        test.run(viewCommand);
        List<CapturedLogEvent> logEntries = test.getAllLogEntries();
        // test that view has logged the incidents
        assertThat(logEntries, JUnitMatchers.hasItems(CapturedLogEvent.apply(Level.INFO,
                "test-match![MatchProtocol.Incident(matchId=test-match, eventId=1, participantRef=null, timestamp=null, eventType=null)]")));
    }
}
