import estimation.DelayEstimation;
import org.junit.Assert;
import org.junit.Test;
import estimation.HttpCommunications;

import java.time.LocalDateTime;

import static org.junit.Assert.fail;

public class HttpCommunicationsTest
{
    private final String MODEL_PATH = "src\\main\\resources\\model.pkl";

    @Test
    public void parseResponseBuildModel_CorrectAccuracy_Equal()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        String jsonString = "{\"accuracy\": 88.96724}";
        double accuracyResult = httpCommunications.parseResponseBuildModel(jsonString);
        Assert.assertEquals(accuracyResult,88.96724,0.0001);
    }

    @Test
    public void parseResponseStringPrediction_SEstimation_doublePercentage_Small_72()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        String jsonString = "{\"prediction\": [\"S\"]}";
        DelayEstimation delayEstimation = httpCommunications.parseResponseStringPrediction(jsonString, 72.62);

        Assert.assertEquals(delayEstimation.getEstimationAccuracyPercentage(),72);
        Assert.assertEquals(delayEstimation.getTypeRange().getEstimationType(), DelayEstimation.EstimationType.Small);
    }

    @Test
    public void parseResponseStringPrediction_MEstimation_integerPercentage_Medium_72()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        String jsonString = "{\"prediction\": [\"M\"]}";
        DelayEstimation delayEstimation = httpCommunications.parseResponseStringPrediction(jsonString,72);

        Assert.assertEquals(delayEstimation.getEstimationAccuracyPercentage(),72);
        Assert.assertEquals(delayEstimation.getTypeRange().getEstimationType(), DelayEstimation.EstimationType.Medium);
    }

    @Test
    public void parseResponseStringPrediction_LEstimation_integerPercentage_Large_93()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        String jsonString = "{\"prediction\": [\"L\"]}";
        DelayEstimation delayEstimation = httpCommunications.parseResponseStringPrediction(jsonString,93.5);

        Assert.assertEquals(delayEstimation.getEstimationAccuracyPercentage(),93);
        Assert.assertEquals(delayEstimation.getTypeRange().getEstimationType(), DelayEstimation.EstimationType.Large);
    }

    @Test
    public void buildModel_accuracyIsReturned()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        double accuracy = httpCommunications.BuildModel();

        Assert.assertTrue(accuracy > 0);
    }

    @Test
    public void Predict_delayEstimationCreated()
    {
        HttpCommunications httpCommunications = new HttpCommunications();
        DelayEstimation delayEstimation = httpCommunications.Predict("Dolittle", LocalDateTime.now());

        Assert.assertNotNull(delayEstimation);
    }

    @Test
    public void httpRequestsCreate_4differentConnections_NoError()
    {
        try
        {
            HttpCommunications httpCommunications = new HttpCommunications();

            httpCommunications.BuildModel();
            httpCommunications.Predict("Dolittle", LocalDateTime.now());
            httpCommunications.Predict("Dolittle", LocalDateTime.now());
            httpCommunications.BuildModel();
        }
        catch (Exception e)
        {
            fail();
        }
    }
}