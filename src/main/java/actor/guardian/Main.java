package actor.guardian;

import actor.match.MatchProtocol;
import actor.match.MatchSupervisor;
import akka.actor.typed.ActorSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final ActorSystem<MatchProtocol.Command> actorSystem = ActorSystem.create(MatchSupervisor.create(), "Guard");

        String fileName = "match_list.csv";
        File file = new File(fileName);

        try {
            Scanner inputStream = new Scanner(file, String.valueOf(StandardCharsets.UTF_8));
            while (inputStream.hasNext()) {
                String data = inputStream.nextLine();
                String[] parsedValues = data.split(",");

                MatchProtocol.Incident incident = MatchProtocol.Incident.of(parsedValues[0], Integer.parseInt(parsedValues[1]), parsedValues[2], parsedValues[3], parsedValues[4]);
                actorSystem.tell(MatchProtocol.Update.of(incident, Optional.empty()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        actorSystem.tell(MatchProtocol.View.of());


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        actorSystem.tell(MatchProtocol.GracefulShutdown.INSTANCE);
    }
}

