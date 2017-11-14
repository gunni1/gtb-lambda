package persistence;

import domain.SessionId;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Verwaltungsoperationen von Trainings-Sessions. Der Zustand über Sitzungen wird nur in der DB gehalten.
 */
public interface TrainingSessionRepository {
    /**
     * Startet eine neue Trainingssitzung
     * @param userId
     * @param startTime
     * @param title
     * @param location
     * @return
     */
    SessionId createSession(String userId, ZonedDateTime startTime, Optional<String> title, Optional<String> location);

    /**
     * Liefert die ID der aktiven Trainingssitzung, sofern gerade eine aktiv ist. Sonst Optional.empty
     * @param userId
     * @return
     */
    Optional<SessionId> getActiveTrainingSessionId(String userId);
}
