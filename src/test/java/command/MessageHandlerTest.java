package command;

import domain.UserId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageHandlerTest
{
    private static final UserId USER_ID = new UserId("Hans");
    private MessageHandler handler;
    private TrainingsRepository mockRepo;


    @Before
    public void setUp()
    {
        handler = new MessageHandler();
    }

    @Test
    public void tokenize()
    {
        BeginTrainingSessionCommand command = mock(BeginTrainingSessionCommand.class);
        String prefix = "/pre";
        when(command.getCommandPrefix()).thenReturn(prefix);
        handler.registerCommand(command);
        handler.handleMessage(USER_ID, prefix + " aaa bbb ccc");
        verify(command).executeCommand(USER_ID, Arrays.asList("aaa", "bbb", "ccc"));
    }
}
