package EntityTest;

import entities.Doctor;
import handlers.Handler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalTime;

public class DoctorTest {

    @BeforeClass
    public static void setUpClass(){
        Handler.printHeadline("Doctor Test");
    }

    @Test
    public void compactDoctorTest() {
        Handler.printHeadline("compact Doctor Test");

        System.out.println("\n" +
                "String doctorsName = \"compact\";\n" +
                "Doctor doctor = new Doctor(doctorsName);\n" +
                "System.out.println(doctor.toString());\n");

        String doctorsName = "compact";
        Doctor doctor = new Doctor(doctorsName);
        System.out.println(doctor.toString());


        System.out.println("Assert.assertEquals(doctorsName, doctor.getName());\n" +
                "Assert.assertNull(doctor.getType());\n" +
                "Assert.assertNull(doctor.getStartTime());\n" +
                "Assert.assertNull(doctor.getEndTime());\n" +
                "Assert.assertEquals(-1, doctor.getInterval());");
        Assert.assertEquals(doctorsName, doctor.getName());
        Assert.assertNull(doctor.getType());
        Assert.assertNull(doctor.getStartTime());
        Assert.assertNull(doctor.getEndTime());
        Assert.assertEquals(-1, doctor.getInterval());
        Handler.printHeadline("compact Doctor Test finished successfully");
    }

    @Test
    public void informativeDoctorTest() {
        Handler.printHeadline("informative Doctor Test");

        System.out.println("String doctorsName = \"informative\";\n" +
                "String doctorsType = \"family\";\n" +
                "LocalTime startTime = LocalTime.parse(\"08:00:00\");\n" +
                "LocalTime endTime = LocalTime.parse(\"12:00:00\");\n" +
                "int interval = 8;\n" +
                "Doctor doctor = new Doctor(doctorsType, doctorsName, startTime, endTime, interval);\n" +
                "System.out.println(doctor.toString());\n");

        String doctorsName = "informative";
        String doctorsType = "family";
        LocalTime startTime = LocalTime.parse("08:00:00");
        LocalTime endTime = LocalTime.parse("12:00:00");
        int interval = 8;

        Doctor doctor = new Doctor(doctorsType, doctorsName, startTime, endTime, interval);
        System.out.println(doctor.toString());

        System.out.println("Assert.assertEquals(Doctor.DoctorType.FAMILY, doctor.getType());\n" +
                "Assert.assertEquals(doctorsName, doctor.getName());\n" +
                "Assert.assertEquals(startTime, doctor.getStartTime());\n" +
                "Assert.assertEquals(endTime, doctor.getEndTime());\n" +
                "Assert.assertEquals(interval, doctor.getInterval());");
        Assert.assertEquals(Doctor.DoctorType.FAMILY, doctor.getType());
        Assert.assertEquals(doctorsName, doctor.getName());
        Assert.assertEquals(startTime, doctor.getStartTime());
        Assert.assertEquals(endTime, doctor.getEndTime());
        Assert.assertEquals(interval, doctor.getInterval());
        Handler.printHeadline("informative Doctor Test finished successfully");
    }

}
