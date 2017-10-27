import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;

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

    public BotRequestHandler() {
        String userName = getenv("bot_username");
        String token = getenv("bot_token");
        Objects.requireNonNull(userName);
        Objects.requireNonNull(token);
        WebHookBotFactory webHookBotFactory = new WebHookBotFactory(token, userName);
        this.responseSender = webHookBotFactory.createSender();
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        Optional<Update> maybeUpdate = parseUpdate(input);
        if (maybeUpdate.isPresent() && maybeUpdate.get().hasMessage())
        {
            Message message = maybeUpdate.get().getMessage();
            String responseText = handleCommand(message.getText().toLowerCase());

            SendMessage sendMessage = new SendMessage()
                    .setChatId(message.getChatId())
                    .setText(responseText);
            System.out.println("Sending message: " + sendMessage);

            sendResponse(sendMessage);
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

    private String handleCommand(String command) {
        switch(command){
            case "/create": return "erzeugt";
            case "/list": return "alle";
            default: return "";
        }
    }

    private Optional<Update> parseUpdate(InputStream inputStream) {
        Update update = null;
        try {
            update = new ObjectMapper().readValue(inputStream, Update.class);
            System.out.println("Update: " + update);
        } catch (Exception e) {
            //TODO: Logging
            System.err.println("Failed to parse update: " + e);
        }
        return Optional.ofNullable(update);
    }
}