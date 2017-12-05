package session;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SessionBuilder
{
    private final String id;
    private final String userId;
    private ZonedDateTime dateTime;
    private Optional<String> maybeTitle;
    private Optional<String> maybeLocation;
    private List<Practice> practices;

    public SessionBuilder(String id, String userId)
    {
        this.id = id;
        this.userId = userId;
        this.maybeLocation = Optional.empty();
        this.maybeTitle = Optional.empty();
        this.dateTime = ZonedDateTime.now();
        this.practices =  new ArrayList<>();
    }

    public SessionBuilder dateTime(ZonedDateTime dateTime)
    {
        this.dateTime = dateTime;
        return this;
    }

    public SessionBuilder title(String title)
    {
        this.maybeLocation = Optional.of(title);
        return this;
    }

    public SessionBuilder location(String location)
    {
        this.maybeTitle = Optional.of(location);
        return this;
    }

    public Session build()
    {
        Session session =  new Session();
        session.setId(id);
        session.setUserId(userId);
        session.setDateTime(dateTime);
        session.setIsActive(true);
        maybeLocation.ifPresent(title -> session.setTitle(title));
        maybeTitle.ifPresent(title -> session.setLocation(title));
        return session;
    }
}
