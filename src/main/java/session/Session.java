package session;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "sessions")
public class Session
{
    private String id;
    private String userId;
    private String location;
    private String title;
    private Boolean isActive;
    private ZonedDateTime dateTime;
    private List<Practice> practices;

    public Session()
    {
        practices = new ArrayList<>();
    }

    @DynamoDBHashKey
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @DynamoDBAttribute
    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    @DynamoDBAttribute
    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    @DynamoDBAttribute
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public ZonedDateTime getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime)
    {
        this.dateTime = dateTime;
    }

    @DynamoDBAttribute
    public List<Practice> getPractices()
    {
        return practices;
    }

    public void setPractices(List<Practice> practices)
    {
        this.practices = practices;
    }

    public Boolean getIsActive()
    {
        return isActive;
    }

    public void setIsActive(Boolean active)
    {
        isActive = active;
    }

    public static class LocalDateTimeConverter implements DynamoDBTypeConverter<String, ZonedDateTime>
    {
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        @Override
        public String convert(ZonedDateTime dateTime)
        {
            return dateTime.format(DATE_FORMAT);
        }

        @Override
        public ZonedDateTime unconvert(String dateString)
        {
            return ZonedDateTime.parse(dateString, DATE_FORMAT);
        }
    }
}
