package handlers;

import algorithms.Algorithms;
import generated.Communication;
import mockers.AlgorithmsMocker;
import mockers.DataBaseMocker;
import mockers.TestBase;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestHandlerTest {
    private final DataBaseMocker db = new DataBaseMocker();
    private final Algorithms algorithms = new AlgorithmsMocker();
    private final RequestHandler requestHandler = new RequestHandler(DataBaseMocker.USER, algorithms, db);

    @Test
    public void testHandleRequestNowSuccess() {
        Communication.C2S.Request request = TestBase.getValidRequest(TestBase.doctors.get(0),
                Communication.C2S.Request.Type.NOW);
        mockers.TestBase.assertSuccessfulResponse(requestHandler.handle(request));
    }

    @Test
    public void testHandleRequestExpectedSuccess() {
        Communication.C2S.Request request = mockers.TestBase.getValidRequest(mockers.TestBase.doctors.get(0),
                Communication.C2S.Request.Type.ESTIMATE);
        mockers.TestBase.assertSuccessfulResponse(requestHandler.handle(request));
    }

    @Test
    public void testHandleRequestNoDoctor() {
        Communication.C2S.Request request = mockers.TestBase.getValidRequest("noDoctor",
                Communication.C2S.Request.Type.ESTIMATE);
        Communication.S2C s2c = requestHandler.handle(request);
        mockers.TestBase.assertErrorResponse(s2c);
        Assert.assertEquals(s2c.getResponse().getErrorCode(), Communication.S2C.Response.ErrorCode.DOCTOR_NOT_FOUND);
    }

    @Test
    public void testHandleRequestNoCurrentData() {
        Communication.C2S.Request request = mockers.TestBase.getValidRequest(mockers.TestBase.doctors.get(3),
                Communication.C2S.Request.Type.NOW);
        Communication.S2C s2c = requestHandler.handle(request);
        mockers.TestBase.assertSuccessfulResponse(s2c);
        Assert.assertTrue(s2c.getResponse().getExpectedDelay().getIsEstimated());
    }

    @Test
    public void testHandleRequestNoCurrentAndNoData() {
        Communication.C2S.Request request = mockers.TestBase.getValidRequest(mockers.TestBase.doctors.get(4),
                Communication.C2S.Request.Type.NOW);
        Communication.S2C s2c = requestHandler.handle(request);
        mockers.TestBase.assertErrorResponse(s2c);
        Assert.assertEquals(s2c.getResponse().getErrorCode(), Communication.S2C.Response.ErrorCode.NO_DATA);
    }

    @Test
    public void testHandleRequestNoData() {
        Communication.C2S.Request request = mockers.TestBase.getValidRequest(mockers.TestBase.doctors.get(4),
                Communication.C2S.Request.Type.ESTIMATE);
        Communication.S2C s2c = requestHandler.handle(request);
        mockers.TestBase.assertErrorResponse(s2c);
        Assert.assertEquals(s2c.getResponse().getErrorCode(), Communication.S2C.Response.ErrorCode.NO_DATA);
    }

}