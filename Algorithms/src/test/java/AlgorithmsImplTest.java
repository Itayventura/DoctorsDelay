import algorithms.Algorithms;
import algorithms.AlgorithmsImpl;
import estimation.DelayEstimation;
import estimation.HttpCommunications;
import org.junit.Assert;
import org.junit.Test;

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
        String existsPath = "scripts\\model.pkl";
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
    public void checkRequestValidation_DoctorExist_ValidTime_NoExceptionThrown() throws Exception
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
    public void checkRequestValidation_InvalidTime_earlyTime_AlgorithmExceptionThrown()
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
            DelayEstimation estimatedDelay = algorithms.getEstimatedDelay("Shirin", LocalDateTime.now());
            Assert.assertEquals(estimatedDelay.getTypeRange().getEstimationType(), expectedEstimationType);
            Assert.assertEquals(estimatedDelay.getEstimationAccuracyPercentage(), expectedAccuracy);
        }
        catch (Algorithms.AlgorithmException e)
        {
            Assert.fail();
        }
    }

    @Test
    public void addReport()
    {
        AlgorithmsImpl algorithms = new AlgorithmsImpl(new DatabaseMocker(), new HttpCommunicationsMocker());
        // TODO
//        algorithms.addReport();
    }
}