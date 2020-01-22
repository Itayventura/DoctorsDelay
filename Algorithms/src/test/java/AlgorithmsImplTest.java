import algorithms.Algorithms;
import algorithms.AlgorithmsImpl;
import estimation.DelayEstimation;
import estimation.HttpCommunications;
import estimation.ModelDetails;
import org.junit.Assert;

import org.junit.Test;

import java.io.File;
import java.time.LocalDateTime;

public class AlgorithmsImplTest
{
    @Test
    public void isModelAlreadyExist_modelNotExist_false()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunications());
        String notExistsPath = "src\\main\\";
        Assert.assertFalse(algorithms.isModelAlreadyExist(notExistsPath));
    }


    @Test
    public void isModelAlreadyExist_modelExist_true()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        String existsPath = "scripts\\model_columns.pkl";
        Assert.assertTrue(algorithms.isModelAlreadyExist(existsPath));
    }

    @Test
    public void shouldModelBeUpdated_modelShouldBeUpdated_true()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        Assert.assertTrue(algorithms.shouldModelBeUpdated(
                LocalDateTime.of(1991, 1, 1, 12, 0, 0)));
    }

    @Test
    public void shouldModelBeUpdated_modelShouldBeUpdated_false()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        Assert.assertFalse(algorithms.shouldModelBeUpdated(LocalDateTime.now()));
    }

    @Test
    public void checkRequestValidation_DoctorExist_ValidTime_NoExceptionThrown()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        try
        {
            algorithms.checkRequestValidation("Dolittle", LocalDateTime.now().plusMinutes(60));
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.fail();
        }
    }

    @Test
    public void checkRequestValidation_DoctorNotExist_AlgorithmExceptionThrown()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        try
        {
            algorithms.checkRequestValidation("Shuki", LocalDateTime.now().plusMinutes(60));
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.assertEquals(e.getReason(), Algorithms.AlgorithmException.Reason.DOCTOR_NOT_EXISTS);
        }
    }

    @Test
    public void checkRequestValidation_InvalidTime_passedTime_AlgorithmExceptionThrown()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        try
        {
            algorithms.checkRequestValidation("Dolittle", LocalDateTime.now().minusMinutes(60));
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.assertEquals(e.getReason(), Algorithms.AlgorithmException.Reason.INVALID_TIME_REQUEST);
        }
    }

    @Test
    public void checkRequestValidation_InvalidTime_EarlyTime_AlgorithmExceptionThrown()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        try
        {
            algorithms.checkRequestValidation("Dolittle", LocalDateTime.now().minusHours(7));
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.assertEquals(e.getReason(), Algorithms.AlgorithmException.Reason.INVALID_TIME_REQUEST);
        }
    }

    @Test
    public void checkRequestValidation_InvalidTime_outOfDateTime_AlgorithmExceptionThrown()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        try
        {
            algorithms.checkRequestValidation("Dolittle", LocalDateTime.now().plusHours(7));
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.assertEquals(e.getReason(), Algorithms.AlgorithmException.Reason.INVALID_TIME_REQUEST);
        }
    }

    @Test
    public void getEstimatedDelay_AsExpected()
    {
        DelayEstimation.EstimationType expectedEstimationType = DelayEstimation.EstimationType.Medium;
        int expectedAccuracy = 85;

        AlgorithmsImpl algorithms = new AlgorithmsImpl(
                new DatabaseMocker(),
                new HttpCommunicationsMocker(expectedEstimationType, expectedAccuracy));
        try
        {
            DelayEstimation estimatedDelay = algorithms.getEstimatedDelay("Dolittle", LocalDateTime.now().plusMinutes(30));
            Assert.assertEquals(estimatedDelay.getTypeRange().getEstimationType(), expectedEstimationType);
            Assert.assertEquals(estimatedDelay.getEstimationAccuracyPercentage(), expectedAccuracy);
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.fail();
        }
    }

    @Test
    public void saveModelDetailsIntoFile_createdFile_NoErrors()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        String tempModelDetailsFilePath = "testFile.txt";

        File file = new File(tempModelDetailsFilePath);
        if(file.exists())
        {
            file.delete();
        }

        algorithms.saveModelDetailsIntoFile(45.3,LocalDateTime.now(), tempModelDetailsFilePath);
        if(!file.exists())
        {
            Assert.fail();
        }
        else
        {
            file.delete();
        }
    }

    @Test
    public void getModelDetailsFromFile_NoFileExist_DefaultModelDetailsValues()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        ModelDetails modelDetails = algorithms.getModelDetailsFromFile("not-a-real-path");
        Assert.assertEquals(modelDetails.getModelAccuracy(), 0, 0);
        Assert.assertNull(modelDetails.getLastModelUpdatedTime());
    }

    @Test
    public void getModelDetailsFromFile_FileExist_RightModelDetailsValues()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        String tempModelDetailsFilePath = "tempModelDetails.txt";

        LocalDateTime expectedTime = LocalDateTime.of(1994, 07, 28, 12, 05, 00);
        double expectedAccuracy = 62.88;
        algorithms.saveModelDetailsIntoFile(expectedAccuracy, expectedTime, tempModelDetailsFilePath);
        ModelDetails modelDetails = algorithms.getModelDetailsFromFile(tempModelDetailsFilePath);

        Assert.assertEquals(expectedTime, modelDetails.getLastModelUpdatedTime());
        Assert.assertEquals(expectedAccuracy, modelDetails.getModelAccuracy(), 0);

        File file = new File(tempModelDetailsFilePath);
        file.delete();
    }
}