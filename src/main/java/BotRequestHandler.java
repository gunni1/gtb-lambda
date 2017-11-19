import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.BeginTrainingSessionCommand;
import command.MessageHandler;
import domain.UserId;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import session.DynDBSessionManager;
import session.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;

import static java.lang.System.getenv;

/**
 * Routet Requests des Telegram Bots.
 */
public class BotRequestHandler implements RequestStreamHandler {

    private final AbsSender responseSender;
    private final MessageHandler messageHandler;
    private final SessionManager sessionManager;

    public BotRequestHandler() {
        String userName = getenv("bot_username");
        String token = getenv("bot_token");
        Objects.requireNonNull(userName);
        Objects.requireNonNull(token);
        WebHookBotFactory webHookBotFactory = new WebHookBotFactory(token, userName);
        this.sessionManager = new DynDBSessionManager();
        this.responseSender = webHookBotFactory.createSender();
        this.messageHandler = new MessageHandler();
        messageHandler.registerCommand(new BeginTrainingSessionCommand(sessionManager));
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Optional<Update> maybeUpdate = parseUpdate(input);
        if (maybeUpdate.isPresent() && maybeUpdate.get().hasMessage())
        {
            Message message = maybeUpdate.get().getMessage();
            UserId userId = new UserId(maybeUpdate.get().getMessage().getFrom().getId());

            String responseText = messageHandler.handleMessage(userId, message.getText().toLowerCase());

            if(!responseText.isEmpty())
            {
                SendMessage sendMessage = new SendMessage()
                        .setChatId(message.getChatId())
                        .setText(responseText);
                System.out.println("Sending message: " + sendMessage);
                sendResponse(sendMessage);
            }
        }
    }

    private void sendResponse(SendMessage sendMessage) {
        try {
            Message message = responseSender.sendMessage(sendMessage);
            System.out.println("Message sent: " + message);
        } catch (Exception e) {
            System.err.println("Failed to send mesage: " + e);
            throw new RuntimeException("Failed to send message!", e);
        }
    }

    private Optional<Update> parseUpdate(InputStream inputStream) {
        Update update = null;
        try {
            update = new ObjectMapper().readValue(inputStream, Update.class);
            System.out.println("Update: " + update);
        } catch (Exception e) {
            System.err.println("Failed to parse update: " + e);
        }
        return Optional.ofNullable(update);
    }
}
