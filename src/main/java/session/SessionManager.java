package session;

import domain.TrainingSession;
import domain.UserId;

import java.util.Optional;

/**
 * Verwaltet Trainingssitzungen über alle Nutzer
 */
public interface SessionManager {

    /**
     * Liefert die aktive SItzung eines Benutzers, sonfern eine existiert.
     *
     * @param userId
     * @return Die Id der aktiven Sitzung, sonfern eine existiert. Sonst Optional.empty
     */
    Optional<TrainingSession> getActiveSession(UserId userId);

    /**
     * Versucht eine neue Trainingssitzung zu erzeugen.
     * Falls bereits eine Existiert beinhaltet das Ergebnis eine Fehlermeldung für den Benutzer.
     *
     * @param userId
     * @param location
     * @return
     */
    SessionCreationResult createSession(UserId userId, Optional<String> title, Optional<String> location);

    /**
     * Beendet die aktive Trainingssitzung.
     *
     * @param userId
     * @return true, wenn erfolgreich beendet, false wenn was nicht klappt.
     */
    boolean endSession(UserId userId);
}
