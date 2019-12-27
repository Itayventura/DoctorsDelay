package EntityTest;

import entities.Appointment;
//import handlers.Handler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AppointmentTest {

    private String appointment_time;
    private String patientId;
    private String doctorsName;

    @BeforeClass
    public static void setUpClass(){
        //Handler.printHeadline("Appointment Test");
    }

    @Before
    public void setUp(){
        //Handler.printHeadline("setUp");
        System.out.println("localDateTime = Timestamp.valueOf(\"2019-11-26 10:40:32\").toLocalDateTime();\n" +
                "doctorsName = \"Doctor\";\n");
        appointment_time = "2019-11-26 10:40:32";
        doctorsName = "Doctor";
    }
    @Test
    public void informativeAppointmentTest() {
        //Handler.printHeadline("informative Appointment Test");

        System.out.println("patientId = \"123456789\";\n");
        patientId = "123456789";

        System.out.println("Appointment appointment = new Appointment(patientId, localDateTime, doctorsName);");
        Appointment appointment = new Appointment(patientId, appointment_time, doctorsName);

        System.out.println("System.out.println(appointment.toString());\n");
        System.out.println(appointment.toString());

        System.out.println("Assert.assertEquals(doctorsName, appointment.getDoctorsName());\n" +
                "Assert.assertEquals(appointment_time, appointment.getAppointmentDateTime().format(Appointment.formatter));\n" +
                "Assert.assertEquals(patientId, appointment.getPatientId());");
        Assert.assertEquals(doctorsName, appointment.getDoctorsName());
        Assert.assertEquals(appointment_time, appointment.getAppointmentDateTime().format(Appointment.formatter));
        Assert.assertEquals(patientId, appointment.getPatientId());
        System.out.println("assertions passed successfully");
        //Handler.printHeadline("informative Appointment Test finished successfully");

    }

    @Test
    public void compactAppointmentTest() {
        //Handler.printHeadline("compact Appointment Test");
        System.out.println("Appointment appointment = new Appointment(localDateTime,doctorsName);");
        Appointment appointment = new Appointment(appointment_time,doctorsName);

        System.out.println("System.out.println(appointment.toString());\n");
        System.out.println(appointment.toString());

        System.out.println("Assert.assertEquals(doctorsName, appointment.getDoctorsName());\n" +
                "Assert.assertEquals(appointment_time, appointment.getAppointmentDateTime().format(Appointment.formatter));\n" +
                "Assert.assertNull(appointment.getPatientId());\n");

        Assert.assertEquals(doctorsName, appointment.getDoctorsName());
        Assert.assertEquals(appointment_time, appointment.getAppointmentDateTime().format(Appointment.formatter));
        Assert.assertNull(appointment.getPatientId());
        System.out.println("assertions passed successfully");
        //Handler.printHeadline("compact Appointment Test finished successfully");
    }

}
