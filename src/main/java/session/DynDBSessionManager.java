package session;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import domain.TrainingSession;
import domain.UserId;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementierung des Sessionmanager für DynamoDB
 */
public class DynDBSessionManager implements SessionManager{

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "sessions";
    private Regions REGION = Regions.EU_CENTRAL_1;
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public DynDBSessionManager() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }

    @Override
    public Optional<TrainingSession> getActiveSession(UserId userId) {
        return null;
    }

    @Override
    public SessionCreationResult createSession(UserId userId, Optional<String> maybeTitle, Optional<String> maybeLocation) {
        String id = UUID.randomUUID().toString();

        Item itemToSave = new Item();
        itemToSave.withString("userId", userId.asString());
        itemToSave.withString("dateTime",ZonedDateTime.now().format(DATE_FORMAT));
        itemToSave.withBoolean("isActive", true);

        maybeTitle.ifPresent(title -> itemToSave.withString("title", title));
        maybeLocation.ifPresent(location -> itemToSave.withString("location", location));

        PutItemSpec putItemSpec = new PutItemSpec().withItem(itemToSave);
        PutItemOutcome outcome = dynamoDb.getTable(DYNAMODB_TABLE_NAME).putItem(putItemSpec);

        return createResult(outcome.getItem());
    }

    private SessionCreationResult createResult(Item outcome) {
        String dateTimeString = outcome.getString("dateTime");
        TrainingSession.Builder builder = TrainingSession.byBuilder(
                new UserId(outcome.getString("userId")), ZonedDateTime.parse(dateTimeString, DATE_FORMAT));
        Optional.ofNullable(outcome.getString("title")).ifPresent(title -> builder.withTitle(title));
        Optional.ofNullable(outcome.getString("location")).ifPresent(location -> builder.withLocation(location));

        return new SessionCreationResult(Optional.of(builder.build()));
    }

    @Override
    public boolean endSession(UserId userId) {
        return false;
    }
}
