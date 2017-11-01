package command;

import persistence.TrainingsRepository;

import java.util.*;

/**
 * Routet unbearbeitete Bot-Messages zu entsprechenden Commands
 */
public class CommandHandler {

    private Collection<BotCommand> commands;

    public CommandHandler(){
        commands = new ArrayList<>();
    }
    /**
     * Versucht eine Nachricht eines Bots als Command zu intertretieren.
     */
    public String executeAsCommand(String botMessage)
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
                response = botCommand.get().executeCommand(tokens);
            }
        }
        return response;

    }

    public void addCommand(BotCommand command){
        commands.add(command);
    }
}
