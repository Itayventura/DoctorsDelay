package HandlerTest;

import db.DataBaseImpl;
import entities.Appointment;
import entities.Entity;
import handlers.AppointmentsHandler;
import handlers.Handler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class AppointmentsHandlerTest {

    static {
        DataBaseImpl.init();
    }

    static class GetUserFutureAppointments extends Thread {
        private int patientId;
        List<Appointment> futureAppointments;
        GetUserFutureAppointments(int patientId) {
            this.patientId = patientId;
        }
        public void run() {
            futureAppointments = appointmentsHandler.getUserFutureAppointments(patientId);
        }

        List<Appointment> getAppointments() {
            return futureAppointments;
        }
    }

    static class GetLastAppointment extends Thread {
        private int patientId;
        private Appointment lastAppointment;
        GetLastAppointment(int patientId) {
            this.patientId = patientId;
        }
        public void run() {
            lastAppointment = appointmentsHandler.getLastAppointment(patientId);
        }

        Appointment getLastAppointment(){
            return lastAppointment;
        }
    }

    private static AppointmentsHandler appointmentsHandler = new AppointmentsHandler();

    @BeforeClass
    public static void setUpClass(){
        Handler.printHeadline("AppointmentsHandlerTest");
    }


    @Test
    public void GetLastAppointmentTest(){
        int num_of_threads = 100;
        GetLastAppointment[] threads = new GetLastAppointment[num_of_threads];
        for(int i = 0; i < num_of_threads; i++) {
            threads[i] = new GetLastAppointment(111111111);
        }
        for (Thread T: threads)
            T.start();
        for (Thread T: threads) {
            try {
                T.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(GetLastAppointment thread: threads)
            Assert.assertNotNull(thread.getLastAppointment());
    }

    @Test
    public void GetUserFutureAppointmentsTest(){
        int num_of_threads = 100;
        GetUserFutureAppointments[] threads = new GetUserFutureAppointments[num_of_threads];
        for(int i = 0; i < num_of_threads; i++) {
            threads[i] = new GetUserFutureAppointments(111111111);
        }
        for (Thread T: threads)
            T.start();
        for (Thread T: threads) {
            try {
                T.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(GetUserFutureAppointments thread: threads)
            Assert.assertEquals(1,thread.getAppointments().size());
    }

    @Test
    public void getUserFutureAppointmentsTest() {
        Handler.printHeadline("getUserFutureAppointmentsTest");

        System.out.println("\nList<Appointment> list = appointmentsHandler.getUserFutureAppointments(111111111);\n" +
                "for(Appointment appointment:list)\n" +
                "   System.out.println(appointment.toString());\n");
        List<Appointment> list = appointmentsHandler.getUserFutureAppointments(111111111);
        for(Appointment appointment:list)
            System.out.println(appointment.toString());

        System.out.println("Assert.assertEquals(1,list.size());");
        Assert.assertEquals(1,list.size());

        System.out.println("Assert.assertEquals(2030, list.get(0).getAppointmentDateTime().getYear());\n");
        Assert.assertEquals(2030, list.get(0).getAppointmentDateTime().getYear());

        Handler.printHeadline("getUserFutureAppointmentsTest finished successfully");
    }

    /** this test checks only for not real patients. addAppointmentsTest checks for real patient*/
    @Test
    public void getLastAppointmentTest(){
        Handler.printHeadline("getLastAppointmentTest");

        System.out.println("\nint[] notPatientIds = PatientHandlerTest.notRealPatients;\n" +
                "        for( int notPatientId: notPatientIds) {\n" +
                "            Appointment appointment = appointmentsHandler.getLastAppointment(notPatientId);\n" +
                "            Assert.assertNull(\"appointment of none existing patient found\", appointment);\n" +
                "        }");
        int[] notPatientIds = PatientHandlerTest.notRealPatients;
        for( int notPatientId: notPatientIds) {
            Appointment appointment = appointmentsHandler.getLastAppointment(notPatientId);
            Assert.assertNull("appointment of none existing patient found", appointment);
        }

        Handler.printHeadline("getLastAppointmentTest finished successfully");
    }

    /** this test add one appointment and checks that it is indeed its patient's last appointment then remove it */
    @Test
    public void addAppointmentsTest(){
        Handler.printHeadline("addAppointmentsTest");

        System.out.println("\nAppointment lastAppointmentBeforeAddition = appointmentsHandler.getLastAppointment(111111111);\n" +
                "Assert.assertNotNull(\"last appointment not found\", lastAppointmentBeforeAddition);\n" +
                "System.out.println(lastAppointmentBeforeAddition.toString());\n");
        Appointment lastAppointmentBeforeAddition = appointmentsHandler.getLastAppointment(111111111);
        Assert.assertNotNull("last appointment not found", lastAppointmentBeforeAddition);
        System.out.println(lastAppointmentBeforeAddition.toString());

        System.out.println("Appointment newAppointmentBeforeAddition = new Appointment(\"111111111\", \"2019-12-31 21:00:00\",\"Luba\");\n" +
                "System.out.println(newAppointmentBeforeAddition.toString());\n");
        Appointment newAppointmentBeforeAddition = new Appointment("111111111", "2019-12-31 21:00:00","Luba");
        System.out.println(newAppointmentBeforeAddition.toString());

        System.out.println("List<Appointment> appointments = new ArrayList<>();\n" +
                "appointments.add(newAppointmentBeforeAddition);\n" +
                "appointmentsHandler.addAppointments(appointments);\n" +
                "Appointment newAppointment = appointmentsHandler.getLastAppointment(111111111);\n" +
                "Assert.assertNotNull(\"new appointment not found\", newAppointment);\n" +
                "Assert.assertEquals(newAppointment.getAppointmentDateTime(),newAppointmentBeforeAddition.getAppointmentDateTime());\n" +
                "Assert.assertEquals(newAppointment.getDoctorsName(),newAppointmentBeforeAddition.getDoctorsName());\n" +
                "System.out.println(newAppointment.toString());\n");
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(newAppointmentBeforeAddition);
        appointmentsHandler.addAppointments(appointments);
        Appointment newAppointment = appointmentsHandler.getLastAppointment(111111111);
        Assert.assertNotNull("new appointment not found", newAppointment);
        Assert.assertEquals(newAppointment.getAppointmentDateTime(),newAppointmentBeforeAddition.getAppointmentDateTime());
        Assert.assertEquals(newAppointment.getDoctorsName(),newAppointmentBeforeAddition.getDoctorsName());
        System.out.println(newAppointment.toString());

        System.out.println("Assert.assertNotEquals(lastAppointmentBeforeAddition.getAppointmentDateTime(), newAppointment.getAppointmentDateTime());\n");
        Assert.assertNotEquals(lastAppointmentBeforeAddition.getAppointmentDateTime(), newAppointment.getAppointmentDateTime());

        System.out.println("appointmentsHandler.removeAppointment(newAppointment.getAppointmentDateTime().format(Entity.formatter), newAppointment.getDoctorsName());\n" +
                "lastAppointmentAfterRemove = appointmentsHandler.getLastAppointment(111111111);\n" +
                "Assert.assertNotEquals(lastAppointmentAfterRemove, newAppointment);\n" +
                "Assert.assertEquals(lastAppointmentBeforeAddition.getAppointmentDateTime(),lastAppointmentAfterRemove.getAppointmentDateTime());\n" +
                "Assert.assertEquals(lastAppointmentBeforeAddition.getDoctorsName(),lastAppointmentAfterRemove.getDoctorsName());\n");
        appointmentsHandler.removeAppointment(newAppointment.getAppointmentDateTime().format(Entity.formatter), newAppointment.getDoctorsName());
        Appointment lastAppointmentAfterRemove = appointmentsHandler.getLastAppointment(111111111);
        Assert.assertNotEquals(lastAppointmentAfterRemove, newAppointment);
        Assert.assertEquals(lastAppointmentBeforeAddition.getAppointmentDateTime(),lastAppointmentAfterRemove.getAppointmentDateTime());
        Assert.assertEquals(lastAppointmentBeforeAddition.getDoctorsName(),lastAppointmentAfterRemove.getDoctorsName());

        System.out.println("Assert.assertNotNull(\"last appointment not found\", lastAppointmentAfterRemove);\n" +
                "System.out.println(lastAppointmentAfterRemove.toString());\n");
        Assert.assertNotNull("last appointment not found", lastAppointmentAfterRemove);
        System.out.println(lastAppointmentAfterRemove.toString());

    }
}