package session;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Practice
{
    private String title;
    private int reps;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getReps()
    {
        return reps;
    }

    public void setReps(int reps)
    {
        this.reps = reps;
    }


}
