package session;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder;
import com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.*;
import com.amazonaws.services.dynamodbv2.xspec.ScanExpressionSpec;
import domain.TrainingSession;
import domain.UserId;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.B;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.BOOL;
import static com.amazonaws.services.dynamodbv2.xspec.ExpressionSpecBuilder.S;

/**
 * Implementierung des Sessionmanager für DynamoDB
 */
public class DynDBSessionManager implements SessionManager
{
    static final String USER_ID = "userId";
    static final String IS_ACTIVE = "isActive";
    static final String DATE_TIME = "dateTime";
    static final String LOCATION = "location";
    static final String TITLE = "title";
    static final String ID = "id";

    private DynamoDB dynamoDb;
    private String SESSION_DB_TABLE = "sessions";
    private Regions REGION = Regions.EU_CENTRAL_1;
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    private AmazonDynamoDBClient client;

    public DynDBSessionManager()
    {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.client = client;
    }

    public DynDBSessionManager(AWSCredentials credentials)
    {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(credentials);
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
        this.client = client;
    }

    @Override
    public Optional<TrainingSession> getActiveSession(UserId userId)
    {
        ScanRequest scanRequest = new ScanRequest(SESSION_DB_TABLE);
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":state", new AttributeValue().withBOOL(true));

        scanRequest.withFilterExpression("isActive = :state").withExpressionAttributeValues(valueMap);
        ScanResult scanResult = client.scan(scanRequest);

        
        if(scanResult.getCount() > 1)
        {
            //TODO: Später Return-Typ ändern und Fehlermeldung an Bentuzer geben
            throw new IllegalStateException("mehr als eine aktive Session vorhanden");
        }
        else if(scanResult.getCount() == 1)
        {
            Map<String, AttributeValue> session = scanResult.getItems().get(0);
            return Optional.of(createSession(session));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public SessionCreationResult createSession(UserId userId, Optional<String> maybeTitle, Optional<String> maybeLocation)
    {
        if (hasActiveSession(userId))
        {
            return new SessionCreationResult(Optional.empty());
        }
        else
        {
            String id = UUID.randomUUID().toString();
            PutItemRequest putItemRequest = new PutItemRequest().withTableName(SESSION_DB_TABLE)
                    .addItemEntry(ID, new AttributeValue().withS(id))
                    .addItemEntry(USER_ID, new AttributeValue().withS(USER_ID))
                    .addItemEntry(DATE_TIME, new AttributeValue().withS(ZonedDateTime.now().format(DATE_FORMAT)))
                    .addItemEntry(IS_ACTIVE, new AttributeValue().withBOOL(true));

            maybeTitle.ifPresent(title -> putItemRequest.addItemEntry(TITLE, new AttributeValue().withS(title)));
            maybeLocation.ifPresent(location -> putItemRequest.addItemEntry(LOCATION, new AttributeValue().withS(location)));
            client.putItem(putItemRequest);
            return new SessionCreationResult(Optional.of(createSession(putItemRequest.getItem())));
        }
    }

    @Override
    public boolean endSession(UserId userId)
    {
        ScanRequest scanRequest = new ScanRequest(SESSION_DB_TABLE);
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":state", new AttributeValue().withBOOL(true));

        scanRequest.withFilterExpression(IS_ACTIVE + " = :state").withExpressionAttributeValues(valueMap);
        ScanResult scanResult = client.scan(scanRequest);

        boolean someThingDone = false;
        for (Map<String, AttributeValue> items: scanResult.getItems())
        {
            String id = items.get(ID).getS();
            UpdateItemRequest updateRequest = new UpdateItemRequest().withTableName(SESSION_DB_TABLE)
                    .addKeyEntry(ID, new AttributeValue().withS(id))
                    .addAttributeUpdatesEntry(IS_ACTIVE, new AttributeValueUpdate().withValue(new AttributeValue().withBOOL(false)));
            client.updateItem(updateRequest);
            someThingDone = true;
        }

        return someThingDone;
    }

    private boolean hasActiveSession(UserId userId)
    {
        ScanRequest scanRequest = new ScanRequest(SESSION_DB_TABLE);
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":state", new AttributeValue().withBOOL(true));

        scanRequest.withFilterExpression(IS_ACTIVE + " = :state").withExpressionAttributeValues(valueMap);
        ScanResult scanResult = client.scan(scanRequest);
        return scanResult.getCount() > 0;
    }

    private TrainingSession createSession(Map<String, AttributeValue>  attributes)
    {
        String dateTimeString = attributes.get(DATE_TIME).getS();
        TrainingSession.Builder builder = TrainingSession.byBuilder(
                attributes.get(ID).getS(), new UserId(attributes.get(USER_ID).getS()),
                ZonedDateTime.parse(dateTimeString, DATE_FORMAT));
        Optional.ofNullable(attributes.get(TITLE)).ifPresent(title -> builder.withTitle(title.getS()));
        Optional.ofNullable(attributes.get(LOCATION)).ifPresent(location -> builder.withTitle(location.getS()));

        return builder.build();
    }
}
