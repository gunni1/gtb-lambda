import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.TelegramWebhookBot;

import java.util.function.Function;

/**
 * Erzeugt WebHooks mit Hilfe der Telegram-API-Bibliothek
 */
public class WebHookBotFactory {
    private final String token;
    private final String userName;

    public WebHookBotFactory(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    /**
     * Erzeugt einen {@link TelegramWebhookBot}-Objekt zum senden des Response.
     */
    public AbsSender createSender()
    {
        Function<Update, BotApiMethod> onUpdateMethod = update -> null;
        return new TelegramWebhookBot() {
            @Override
            public String getBotToken() {
                return token;
            }

            @Override
            public BotApiMethod onWebhookUpdateReceived(Update update) {
                return onUpdateMethod.apply(update);
            }

            @Override
            public String getBotUsername() {
                return userName;
            }

            @Override
            public String getBotPath() {
                return userName;
            }
        };
    }
}
