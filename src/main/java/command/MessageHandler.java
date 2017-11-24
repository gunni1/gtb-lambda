package command;

import domain.UserId;

import java.util.*;

/**
 * Routet unbearbeitete Bot-Messages zu entsprechenden Commands
 */
public class MessageHandler
{

    private Collection<BotCommand> commands;

    public MessageHandler(){
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
                //TODO: Entscheide anahand des DB Zustands (Session) was gemacht wird
            }
        }
        return response;

    }

    public void registerCommand(BotCommand command){
        commands.add(command);
    }
}
