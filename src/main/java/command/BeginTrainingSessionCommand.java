package command;

import domain.UserId;
import session.SessionManager;

import java.util.List;

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
        //TODO: Wenn aktive sitzung existiert, fehler ausgeben, sonst neue beginnen
        return null;
    }
}
