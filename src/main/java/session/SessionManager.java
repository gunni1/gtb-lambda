package session;

import domain.UserId;

import java.util.Optional;

/**
 * Verwaltet Trainingssitzungen über alle Nutzer
 */
public interface SessionManager
{
    Optional<Session> getActiveSession(UserId userId);

    SessionCreationResult createSession(UserId userId, Optional<String> title, Optional<String> location);

    boolean endSession(UserId userId);

    Session addPractice(String sessionId, Practice practice);
}