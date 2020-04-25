package actor.match;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import java.util.HashMap;
import java.util.Map;

public class Match extends AbstractBehavior<MatchProtocol.Command> {

    MatchProtocol.Confirmation confirmation;

    private Map<Integer, MatchProtocol.Incident> eventToIncidentMap = new HashMap<>();

    public static Behavior<MatchProtocol.Command> create(String id) {
        Behavior<MatchProtocol.Command> match = Behaviors.setup(context -> new Match(context, id));
        return Behaviors.supervise(match).onFailure(SupervisorStrategy.restart());
    }

    private final String id;
    private Match(ActorContext<MatchProtocol.Command> context, String id) {
        super(context);
        this.id = id;
        confirmation = MatchProtocol.Confirmation.of(id);
    }

    @Override
    public Receive<MatchProtocol.Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(MatchProtocol.Update.class, this::onUpdate)
                .onMessage(MatchProtocol.View.class, this::onView)
                .onSignal(PostStop.class, signal -> onPostStop())
                .build();
    }

    private Behavior<MatchProtocol.Command> onUpdate(MatchProtocol.Update command) {
        eventToIncidentMap.put(command.getIncident().getEventId(), command.getIncident());
        command.replyTo.ifPresent(supervisorActor -> supervisorActor.tell(confirmation));
        return this;
    }

    private Behavior<MatchProtocol.Command> onView(MatchProtocol.View command) {
        getContext().getLog().info("{}!{}", id, eventToIncidentMap.values());
        return this;
    }

    private Behavior<MatchProtocol.Command> onPostStop() {
        getContext().getSystem().log().info("Inside onPostStop() of a match actor with id " + "\"" + this.id + "\"");
        return this;
    }

}