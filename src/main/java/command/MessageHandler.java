package command;

import domain.Practice;
import domain.TrainingSession;
import domain.UserId;
import session.SessionManager;

import java.util.*;

/**
 * Routet unbearbeitete Bot-Messages zu entsprechenden Commands
 */
public class MessageHandler
{
    private final SessionManager sessionManager;
    private Collection<BotCommand> commands;

    public MessageHandler(SessionManager sessionManager){
        this.sessionManager = sessionManager;
        commands = new ArrayList<>();
    }
    /**
     * Versucht eine Nachricht eines Bots als Command zu intertretieren.
     */
    public String handleMessage(UserId userId, String botMessage)
    {
        String response = "";
        LinkedList<String> tokens = new LinkedList<>(Arrays.asList(botMessage.split(" ")));

        if(!tokens.isEmpty())
        {
            String prefix = tokens.removeFirst();
            System.out.println("Command-Prefix: " + prefix);
            Optional<BotCommand> botCommand = commands.stream().filter(command ->
                    prefix.equalsIgnoreCase(command.getCommandPrefix())).findFirst();
            if(botCommand.isPresent())
            {
                response = botCommand.get().executeCommand(userId, tokens);
            }
            else
            {
                Optional<TrainingSession> activeSession = sessionManager.getActiveSession(userId);
                if(activeSession.isPresent())
                {
                    //TODO: Parsen
                    Practice practice = new Practice();
                    activeSession.get().addPractice(practice);
                    //TODO: DB Update
                }
                else
                {
                    response = "Keine aktive Sitzung. Mit /start eine neue Sitzung beginnen.";
                }
            }
        }
        return response;

    }

    public void registerCommand(BotCommand command){
        if(commands.stream().anyMatch(actual -> actual.getCommandPrefix().equalsIgnoreCase(command.getCommandPrefix())))
        {
            throw new IllegalArgumentException("prefix " + command.getCommandPrefix() + " already registered");
        }
        commands.add(command);
    }
}
