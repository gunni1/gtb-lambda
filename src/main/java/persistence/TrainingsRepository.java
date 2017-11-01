package persistence;

import java.time.ZonedDateTime;

/**
 * Speichern und laden von Trainings
 */
public interface TrainingsRepository {
    /**
     * Erzeugt ein neues domain.Training
     */
    String createTraining(String title, String location, ZonedDateTime trainingTime);
}
