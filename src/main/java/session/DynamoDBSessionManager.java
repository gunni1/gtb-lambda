package session;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import domain.UserId;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class DynamoDBSessionManager implements SessionManager
{
    static final String USER_ID = "userId";
    static final String IS_ACTIVE = "isActive";
    static final String DATE_TIME = "dateTime";
    static final String LOCATION = "location";
    static final String TITLE = "title";
    static final String ID = "id";
    static final String PRACTICES = "practices";

    private String SESSION_DB_TABLE = "sessions";
    private Regions REGION = Regions.EU_CENTRAL_1;
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    DynamoDBMapper mapper;

    public DynamoDBSessionManager()
    {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        mapper = new DynamoDBMapper(client);
    }

    public DynamoDBSessionManager(AWSCredentials credentials)
    {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
        client.setRegion(Region.getRegion(REGION));
        mapper = new DynamoDBMapper(client);
    }

    public Optional<Session> getActiveSession(UserId userId)
    {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        scanExpression.addFilterCondition(USER_ID, new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue(userId.asString())));
        scanExpression.addFilterCondition(IS_ACTIVE, new Condition().withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withN("1")));
        PaginatedScanList<Session> scanResult = mapper.scan(Session.class, scanExpression);
        return scanResult.stream().findFirst();
    }

    public SessionCreationResult createSession(UserId userId, Optional<String> title, Optional<String> location)
    {
        if(getActiveSession(userId).isPresent())
        {
            return SessionCreationResult.byError("Es existiert bereits ein aktives Training. Beende es, bevor du ein neues startest.");
        }
        String id = UUID.randomUUID().toString();
        Session session = new SessionBuilder(id, userId.asString()).build();
        mapper.save(session);
        return SessionCreationResult.bySuccess(session);
    }

    public boolean endSession(UserId userId)
    {
        Optional<Session> activeSession = getActiveSession(userId);
        if(activeSession.isPresent())
        {
            Session session = activeSession.get();
            session.setIsActive(false);
            mapper.save(session);
            return true;
        }
        else
        {
            return false;
        }
    }

    public Session addPractice(String sessionId, Practice practice)
    {
        Session session = mapper.load(Session.class, sessionId);
        session.getPractices().add(practice);
        mapper.save(session);
        return session;
    }
}
