package persistence;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.System.getenv;

public class DynamoDBRepository implements TrainingsRepository{

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "trainings";
    private Regions REGION = Regions.EU_CENTRAL_1;
    private DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public DynamoDBRepository() {
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }

    @Override
    public String createTraining(String title, String location, ZonedDateTime trainingTime) {
        PutItemSpec item = new PutItemSpec().withItem(new Item().withString("title", title)
                .withString("location", location)
                .withString("time", trainingTime.format(DATE_FORMAT)));
        PutItemOutcome outcome = dynamoDb.getTable(DYNAMODB_TABLE_NAME).putItem(item);

        System.out.println(outcome.getPutItemResult().toString());

        return outcome.getItem().toString();
    }
}
