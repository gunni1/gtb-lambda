package command;

import domain.UserId;

import java.util.List;

/**
 * Kapselt ein Kommando, auf das der Bot reagieren soll.
 */
public interface BotCommand {
    /**
     * Liefert das einleitende Token, mit dem das Command registriert, bzw. bei welchem Text das Command ausgeführt  werden soll
     */
    String getCommandPrefix();

    /**
     * Führt die Logik des Commands aus und liefert das Resultat für die Bot-Antwort als String zurück.
     * @param userId Itenditifkation des Telegram-Nutzers
     * @param arguments Übergebene Argumente für das Command
     */
    String executeCommand(UserId userId, List<String> arguments);
}
