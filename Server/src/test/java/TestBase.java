import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBase {
    protected static List<String> doctors = Arrays.asList("doctor0", "doctor1", "doctor2", "doctor3", "doctor4");
    protected static Map<String, Integer> doctors2curDelays = new HashMap<String, Integer>() {{
        put(doctors.get(0), 15);
        put(doctors.get(1), 12);
        put(doctors.get(2), 43);
    }};
    protected static Map<String, Integer> doctors2expectedDelays = new HashMap<String, Integer>() {{
        put(doctors.get(0), 10);
        put(doctors.get(1), 21);
        put(doctors.get(2), 37);
        put(doctors.get(3), 22);
    }};

    protected static boolean doctorExists(String doctorName) {
        return doctors.contains(doctorName);
    }

    protected static Communication.C2S.Request getValidRequest(String doctorsName, Communication.C2S.Request.Type type) {
        return Communication.C2S.Request.newBuilder()
                .setDoctorsName(doctorsName)
                .setType(type).build();
    }

    protected static void assertSuccessfulResponse(Communication.S2C s2c) {
        Assert.assertFalse(s2c.getFinish());
        Communication.S2C.Response response = s2c.getResponse();
        Assert.assertEquals(Communication.S2C.Response.Status.SUCCESSFUL, response.getStatusCode());
        Assert.assertEquals("", response.getErrorMessage());
        Assert.assertTrue(response.hasExpectedDelay());
    }

    protected static void assertErrorResponse(Communication.S2C s2c) {
        Assert.assertFalse(s2c.getFinish());
        Communication.S2C.Response response = s2c.getResponse();
        Assert.assertEquals(Communication.S2C.Response.Status.FAILURE, response.getStatusCode());
    }

}
