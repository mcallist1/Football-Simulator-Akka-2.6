package actor.match;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Optional;

public interface MatchProtocol {
    interface Command{}
    interface MatchData{}

    @AllArgsConstructor(staticName = "of")
    @Getter
    @EqualsAndHashCode
    @ToString
    class Update implements Command {
        private Incident incident;
        Optional<ActorRef<Command>> replyTo;
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    @EqualsAndHashCode
    @ToString
    class Confirmation implements Command {
        String matchId;
    }


    enum GracefulShutdown implements MatchProtocol.Command {
        INSTANCE
    }

    @AllArgsConstructor(staticName = "of")
    @Getter
    @EqualsAndHashCode
    @ToString
    class View implements Command {}

    @AllArgsConstructor (staticName = "of")
    @Getter
    @EqualsAndHashCode
    @ToString
    class Incident implements MatchProtocol.MatchData {
        private String matchId;
        private int eventId;
        private String participantRef;
        private String timestamp;
        private String eventType;
    }

}
