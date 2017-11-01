package command;

import org.junit.Before;
import org.junit.Test;
import persistence.TrainingsRepository;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandHandlerTest {

    private CommandHandler handler;
    private TrainingsRepository mockRepo;

    @Before
    public void setUp()
    {
        handler = new CommandHandler();
    }

    @Test
    public void tokenize()
    {
        CreateTrainingCommand command = mock(CreateTrainingCommand.class);
        String prefix = "/pre";
        when(command.getCommandPrefix()).thenReturn(prefix);
        handler.addCommand(command);
        handler.executeAsCommand(prefix + " aaa bbb ccc");
        verify(command).executeCommand(Arrays.asList("aaa", "bbb", "ccc"));
    }
}
