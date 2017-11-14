package domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ein Training bestehend aus mehreren Übungen
 */
public class TrainingSession {
    private UserId userId;
    private Optional<String> location;
    private Optional<String> title;
    private ZonedDateTime dateTime;
    private List<Practice> practices;

    public TrainingSession(UserId userId, Optional<String> location, Optional<String> title,
                           ZonedDateTime dateTime, List<Practice> practices) {
        this.userId = userId;
        this.location = location;
        this.title = title;
        this.dateTime = dateTime;
        this.practices = practices;
    }

    public UserId getUserId() {
        return userId;
    }

    public Optional<String> getLocation() {
        return location;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public List<Practice> getPractices() {
        return practices;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public static Builder byBuilder(UserId userId, ZonedDateTime dateTime)
    {
        return new Builder(userId, dateTime);
    }

    public static class Builder {
        private UserId userId;
        private Optional<String> location;
        private ZonedDateTime dateTime;
        private List<Practice> practices;
        private Optional<String> title;

        private Builder(UserId userId, ZonedDateTime dateTime) {
            this.userId = userId;
            this.dateTime = dateTime;
            this.location = Optional.empty();
            this.practices = new ArrayList<>();
        }

        public Builder withLocation(String location) {
            this.location = Optional.of(location);
            return this;
        }

        public Builder withTitle(String title) {
            this.title = Optional.of(title);
            return this;
        }

        public TrainingSession build() {
            return new TrainingSession(userId, location, title, dateTime, practices);
        }
    }
}
