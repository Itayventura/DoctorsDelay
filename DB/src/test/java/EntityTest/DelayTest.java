package EntityTest;

import entities.Delay;
import entities.Entity;
import handlers.Handler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class DelayTest {

    private int patientID;
    private String ts;
    private int delayInMinutes;
    private Entity.Type type;

    @BeforeClass
    public static void setUpClass(){
        Handler.printHeadline("Delay Test");

    }

    @Before
    public void setUp(){
        Handler.printHeadline("setUp");
        System.out.println("patientID = 111111111;\n" +
                "ts = (\"2016-11-09 10:30:45\");\n" +
                "delayInMinutes = 16;\n");
        patientID = 111111111;
        ts = ("2016-11-09 10:30:45");
        delayInMinutes = 16;
    }

    @Test
    public void DelayFirstTest() {
        Handler.printHeadline("Delay First Test");

        System.out.println("type =  Entity.Type.USER;\n" +
                "Delay delay = new Delay(delayInMinutes,ts, type,patientID);\n");
        type =  Entity.Type.USER;
        Delay delay = new Delay(delayInMinutes,ts, type,patientID);

        System.out.println("System.out.println(delay.toString());\n");

        System.out.println(delay.toString());

        System.out.println("Assert.assertEquals(ts, delay.getReportTimestamp().format(formatter));\n" +
                "Assert.assertEquals(delayInMinutes, delay.getReportedDelay());\n" +
                "Assert.assertEquals(patientID, delay.getPatientId());\n" +
                "Assert.assertEquals(type, delay.getReportType());");
        Assert.assertEquals(ts, delay.getReportTimestamp().format(Delay.formatter));
        Assert.assertEquals(delayInMinutes, delay.getReportedDelay());
        Assert.assertEquals(patientID, delay.getPatientId());
        Assert.assertEquals(type, delay.getReportType());
        Handler.printHeadline("Delay First Test finished successfully");

    }

    @Test
    public void DelaySecondTest() {
        Handler.printHeadline("Delay Second Test");

        System.out.println("SimpleDateFormat sdf = new SimpleDateFormat(Delay.TIMESTAMP_FORMAT);\n" +
                "Timestamp timestamp =  Timestamp.valueOf (\"2016-11-09 10:30:45\");\n" +
                "ts = sdf.format(timestamp);\n" +
                "type = Entity.Type.FEEDBACK;");
        SimpleDateFormat sdf = new SimpleDateFormat(Delay.TIMESTAMP_FORMAT);
        Timestamp timestamp =  Timestamp.valueOf ("2016-11-09 10:30:45");
        ts = sdf.format(timestamp);
        type = Entity.Type.FEEDBACK;

        System.out.println("Delay delay = new Delay(delayInMinutes,ts, type);\n");
        Delay delay = new Delay(delayInMinutes,ts, type);

        System.out.println("System.out.println(delay.toString());\n");
        System.out.println(delay.toString());

        System.out.println("Assert.assertEquals(ts, delay.getReportTimestamp().format(formatter));\n" +
                "Assert.assertEquals(delayInMinutes, delay.getReportedDelay());\n" +
                "Assert.assertEquals(-1, delay.getPatientId());\n" +
                "Assert.assertEquals(type, delay.getReportType());");
        Assert.assertEquals(ts, delay.getReportTimestamp().format(Delay.formatter));
        Assert.assertEquals(delayInMinutes, delay.getReportedDelay());
        Assert.assertEquals(-1, delay.getPatientId());
        Assert.assertEquals(type, delay.getReportType());
        Handler.printHeadline("Delay Second Test finished successfully");

    }


    @Test
    public void DelayThirdTest() {
        Handler.printHeadline("Delay Third Test");

        System.out.println("type = Entity.Type.EXPERT;");
        type = Entity.Type.EXPERT;

        System.out.println("Delay delay = new Delay(delayInMinutes, type,patientID);");
        Delay delay = new Delay(delayInMinutes, type,patientID);

        System.out.println("System.out.println(delay.toString());\n");
        System.out.println(delay.toString());

        System.out.println("Assert.assertNull(delay.getReportTimestamp());\n" +
                "Assert.assertEquals(delayInMinutes, delay.getReportedDelay());\n" +
                "Assert.assertEquals(patientID, delay.getPatientId());\n" +
                "Assert.assertEquals(type, delay.getReportType());");
        Assert.assertNull(delay.getReportTimestamp());
        Assert.assertEquals(delayInMinutes, delay.getReportedDelay());
        Assert.assertEquals(patientID, delay.getPatientId());
        Assert.assertEquals(type, delay.getReportType());
        Handler.printHeadline("Delay Third Test finished successfully");

    }

    @Test
    public void localDateTimeFormat(){

        System.out.println("LocalDateTime now = LocalDateTime.now();\n" +
                "System.out.println(now);");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        System.out.println("\nString timestamp = now.format(Delay.formatter);\n" +
                "System.out.println(timestamp);");
        String timestamp = now.format(Delay.formatter);
        System.out.println(timestamp);
        System.out.println("\nDelay delay = new Delay(delayInMinutes, timestamp, type);\n" +
                "System.out.println(delay.toString());\n");
        Delay delay = new Delay(delayInMinutes, timestamp, type);
        System.out.println(delay.toString());
    }
}
