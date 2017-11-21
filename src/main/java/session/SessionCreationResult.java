package session;

import domain.TrainingSession;

import java.util.Optional;

/**
 * Ergebnis einer Sitzungserzeugung.
 */
public class SessionCreationResult {
    private Optional<TrainingSession> maybeSession;

    private Optional<String> maybeError;

    public SessionCreationResult(Optional<TrainingSession> maybeTrainingsSession)
    {
        this.maybeSession = maybeTrainingsSession;
        if(!maybeTrainingsSession.isPresent())
        {
            maybeError = Optional.of("Es konnte keine neue Sitzung gestartet werden.");
        }
        else
        {
            maybeError = Optional.empty();
        }
    }

    public Optional<TrainingSession> getMaybeSession()
    {
        return maybeSession;
    }

    public Optional<String> getMaybeError()
    {
        return maybeError;
    }
}
