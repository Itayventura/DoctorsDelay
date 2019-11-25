import org.junit.Assert;
import org.junit.Test;

public class AlgorithmsHandlerTest {
    private AlgorithmsHandler algorithmsHandler = new AlgorithmsHandler(new AlgorithmsMocker());

    @Test
    public void testHandleRequestNowSuccess() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(0),
                Communication.C2S.Request.Type.NOW);
        TestBase.assertSuccessfulResponse(algorithmsHandler.handleRequest(request));
    }

    @Test
    public void testHandleRequestExpectedSuccess() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(0),
                Communication.C2S.Request.Type.ESTIMATE);
        TestBase.assertSuccessfulResponse(algorithmsHandler.handleRequest(request));
    }

    @Test
    public void testHandleRequestNoDoctor() {
        Communication.C2S.Request request = TestBase.getValidRequest("noDoctor",
                Communication.C2S.Request.Type.ESTIMATE);
        Communication.S2C s2c = algorithmsHandler.handleRequest(request);
        TestBase.assertErrorResponse(s2c);
        Assert.assertTrue(s2c.getResponse().getErrorMessage().contains("Doctor"));
    }

    @Test
    public void testHandleRequestNoCurrentData() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(3),
                Communication.C2S.Request.Type.NOW);
        Communication.S2C s2c = algorithmsHandler.handleRequest(request);
        TestBase.assertSuccessfulResponse(s2c);
        Assert.assertTrue(s2c.getResponse().getExpectedDelay().getIsEstimated());
    }

    @Test
    public void testHandleRequestNoCurrentAndNoData() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(4),
                Communication.C2S.Request.Type.NOW);
        Communication.S2C s2c = algorithmsHandler.handleRequest(request);
        TestBase.assertErrorResponse(s2c);
        Assert.assertTrue(s2c.getResponse().getErrorMessage().contains("Data"));
    }

    @Test
    public void testHandleRequestNoData() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(4),
                Communication.C2S.Request.Type.ESTIMATE);
        Communication.S2C s2c = algorithmsHandler.handleRequest(request);
        TestBase.assertErrorResponse(s2c);
        Assert.assertTrue(s2c.getResponse().getErrorMessage().contains("Data"));
    }

}