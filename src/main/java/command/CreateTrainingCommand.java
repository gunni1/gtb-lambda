package command;

import persistence.DynamoDBRepository;
import persistence.TrainingsRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Erzeugt ein neues domain.Training.
 */
public class CreateTrainingCommand implements BotCommand {

    private final TrainingsRepository repo;

    public CreateTrainingCommand(TrainingsRepository repo) {
        this.repo = repo;
    }

    @Override
    public String getCommandPrefix() {
        return "/neu";
    }

    @Override
    public String executeCommand(List<String> arguments) {
        //TODO: Dummy in der DynamoDB erzeugen
        repo.createTraining("Training", "Bootshaus", ZonedDateTime.parse("2017-12-01T18:15:00+01:00[Europe/Paris]"));

        return null;
    }
}
