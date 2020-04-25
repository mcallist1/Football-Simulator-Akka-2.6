package actor.match;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public  class MatchSupervisor extends AbstractBehavior<MatchProtocol.Command> {

    Map<String, ActorRef<MatchProtocol.Command>> matches;

    public static Behavior<MatchProtocol.Command> create() {
        return Behaviors.setup(MatchSupervisor::new);
    }

    public MatchSupervisor(ActorContext<MatchProtocol.Command> context) {
        super(context);
        matches = new HashMap<>();
    }

    @Override
    public Receive<MatchProtocol.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(MatchProtocol.Update.class, this::onUpdateOrCreateMatch)
                .onMessage(MatchProtocol.View.class, this::onViewMatches)
                .onMessage(MatchProtocol.Confirmation.class, this::onConfirmation)
                .onMessage(MatchProtocol.GracefulShutdown.class, message -> onGracefulShutdown())
                .build();
    }

    private Behavior<MatchProtocol.Command> onUpdateOrCreateMatch(MatchProtocol.Update update) {
        String matchId = update.getIncident().getMatchId();

        if (!matches.containsKey(matchId)) {
            matches.put(matchId, getContext().spawn(Match.create(matchId), matchId));
        }
        ActorRef<MatchProtocol.Command> matchActor = matches.get(matchId);
        matchActor.tell(MatchProtocol.Update.of(update.getIncident(), Optional.of(getContext().getSelf())));

        return this;
    }

    private Behavior<MatchProtocol.Command> onViewMatches(MatchProtocol.View view) {
        matches.values().forEach(m -> m.tell(MatchProtocol.View.of()));
        return this;
    }

    private Behavior<MatchProtocol.Command> onGracefulShutdown() {
        getContext().getSystem().log().info("Initialting graceful shutdown...");
        return Behaviors.stopped(() -> getContext().getSystem().log().info("Cleanup!"));
    }

    private Behavior<MatchProtocol.Command> onConfirmation(MatchProtocol.Confirmation confirmation) {
        System.out.println("Confirmed update/create " + confirmation.matchId);
        return this;
    }
}





