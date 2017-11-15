package session;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import domain.TrainingSession;
import domain.UserId;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementierung des Sessionmanager für DynamoDB
 */
public class DynDBSessionManager implements SessionManager
{

    private DynamoDB dynamoDb;
    private String SESSION_DB_TABLE = "sessions";
    private Regions REGION = Regions.EU_CENTRAL_1;
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public DynDBSessionManager()
    {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }

    @Override
    public Optional<TrainingSession> getActiveSession(UserId userId)
    {
        Table table = dynamoDb.getTable(SESSION_DB_TABLE);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("userId = :v_userId and isActive = :v_isAvtive")
                .withValueMap(new ValueMap().withString(":v_userId", userId.asString()).withBoolean(":v_isActive", true))
                .withConsistentRead(true);

        ItemCollection<QueryOutcome> items = table.query(querySpec);
        if(items.getAccumulatedItemCount() > 1)
        {
            //TODO: Später Return-Typ ändern und Fehlermeldung an Bentuzer geben
            throw new IllegalStateException("mehr als eine aktive Session vorhanden");
        }
        else if(items.getAccumulatedItemCount() == 1)
        {
            TrainingSession session = createSession(items.iterator().next());
            return Optional.of(session);
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

            Item itemToSave = new Item();
            itemToSave.withString("id", id);
            itemToSave.withString("userId", userId.asString());
            itemToSave.withString("dateTime", ZonedDateTime.now().format(DATE_FORMAT));
            itemToSave.withBoolean("isActive", true);

            maybeTitle.ifPresent(title -> itemToSave.withString("title", title));
            maybeLocation.ifPresent(location -> itemToSave.withString("location", location));

            PutItemSpec putItemSpec = new PutItemSpec().withItem(itemToSave);
            PutItemOutcome outcome = dynamoDb.getTable(SESSION_DB_TABLE).putItem(putItemSpec);

            return new SessionCreationResult(Optional.of(createSession(outcome.getItem())));
        }
    }

    private boolean hasActiveSession(UserId userId)
    {
        Table table = dynamoDb.getTable(SESSION_DB_TABLE);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("userId = :v_userId and isActive = :v_isAvtive")
                .withValueMap(new ValueMap().withString(":v_userId", userId.asString()).withBoolean(":v_isActive", true))
                .withConsistentRead(true);

        ItemCollection<QueryOutcome> items = table.query(querySpec);
        return items.getAccumulatedItemCount() > 0;
    }

    private TrainingSession createSession(Item item)
    {
        String dateTimeString = item.getString("dateTime");
        TrainingSession.Builder builder = TrainingSession.byBuilder(
                item.getString("id"), new UserId(item.getString("userId")),
                ZonedDateTime.parse(dateTimeString, DATE_FORMAT));
        Optional.ofNullable(item.getString("title")).ifPresent(title -> builder.withTitle(title));
        Optional.ofNullable(item.getString("location")).ifPresent(location -> builder.withLocation(location));
        return builder.build();
    }

    @Override
    public boolean endSession(UserId userId)
    {
        Table table = dynamoDb.getTable(SESSION_DB_TABLE);
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("userId = :v_userId and isActive = :v_isAvtive")
                .withValueMap(new ValueMap().withString(":v_userId", userId.asString()).withBoolean(":v_isActive", true))
                .withConsistentRead(true);
        ItemCollection<QueryOutcome> items = table.query(querySpec);

        //TODO: Query bauen um sessions inaktiv zu setzen
        for (Iterator it = items.iterator(); it.hasNext(); )
        {

        }

        return true;
    }
}
