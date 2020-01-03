package mockers;

import communications.Communication;
import estimation.DelayEstimation;
import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBase {
    public static List<String> doctors = Arrays.asList("doctor0", "doctor1", "doctor2", "doctor3", "doctor4");
    protected static Map<String, DelayEstimation> doctors2curDelays = new HashMap<String, DelayEstimation>() {{
        put(doctors.get(0), new DelayEstimation(DelayEstimation.EstimationType.Small, 40));
        put(doctors.get(1), new DelayEstimation(DelayEstimation.EstimationType.Medium, 70));
        put(doctors.get(2), new DelayEstimation(DelayEstimation.EstimationType.Large, 50));
    }};
    protected static Map<String, DelayEstimation> doctors2expectedDelays = new HashMap<String, DelayEstimation>() {{
        put(doctors.get(0), new DelayEstimation(DelayEstimation.EstimationType.Medium, 80));
        put(doctors.get(1), new DelayEstimation(DelayEstimation.EstimationType.Small, 70));
        put(doctors.get(2), new DelayEstimation(DelayEstimation.EstimationType.Large, 50));
        put(doctors.get(3), new DelayEstimation(DelayEstimation.EstimationType.Medium, 30));
    }};

    protected static boolean doctorExists(String doctorName) {
        return doctors.contains(doctorName);
    }

    public static Communication.C2S.Request getValidRequest(String doctorsName, Communication.C2S.Request.Type type) {
        return Communication.C2S.Request.newBuilder()
                .setDoctorsName(doctorsName)
                .setType(type).build();
    }

    public static void assertSuccessfulResponse(Communication.S2C s2c) {
        Assert.assertFalse(s2c.getFinish());
        Communication.S2C.Response response = s2c.getResponse();
        Assert.assertEquals(Communication.S2C.Response.Status.SUCCESSFUL, response.getStatusCode());
        Assert.assertTrue(response.hasExpectedDelay());
    }

    public static void assertErrorResponse(Communication.S2C s2c) {
        Assert.assertFalse(s2c.getFinish());
        Communication.S2C.Response response = s2c.getResponse();
        Assert.assertEquals(Communication.S2C.Response.Status.FAILURE, response.getStatusCode());
    }

}
