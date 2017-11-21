package command;

import domain.TrainingSession;
import domain.UserId;
import session.SessionCreationResult;
import session.SessionManager;

import java.util.List;
import java.util.Optional;

public class BeginTrainingSessionCommand implements BotCommand
{
    private final SessionManager sessionManager;

    public BeginTrainingSessionCommand(SessionManager sessionManager)
    {
        this.sessionManager = sessionManager;
    }

    @Override
    public String getCommandPrefix()
    {
        return "/start";
    }

    @Override
    public String executeCommand(UserId userId, List<String> arguments)
    {
        System.out.println("user: "+ userId.asString() + " send start with " + arguments.toString());
        //TODO: Location und Title aus argumenten ermitteln

        SessionCreationResult creationResult = sessionManager.createSession(userId, Optional.empty(), Optional.empty());
        Optional<TrainingSession> maybeSession = creationResult.getMaybeSession();

        if(maybeSession.isPresent())
        {
            return "und los...";
        }
        else
        {
            return creationResult.getMaybeError().get();
        }
    }
}
