package DbTest;

import db.DataBase;
import db.DataBaseImpl;
import entities.*;
import handlers.Handler;
import org.junit.*;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DataBaseImplTest {

    static {
        DataBaseImpl.init();
    }

    private static DataBaseImpl db = new DataBaseImpl();
    private static String[] realDoctors;
    private static String[] notRealDoctors;

    @BeforeClass
    public static void setUpClass(){
        realDoctors = new String[]{
                "benben",
                "Ben Yehuda",
                "Luba",
                "Meir",
                "AMPM",
                "Ronen Schwizer",
                "Yafa",
                "Nordau",
                "Ben Gurion",
                "Dizingof",
                "Haim Levanon",
                "Brodetzki"
        };

        notRealDoctors = new String[]{ "Ientura",
                "Idfgra",
                "Ron Ban",
                "Gal Gal",
                "Itay Mor",
                ""};
    }

    @Test
    public void addReportPatientDoesntExistTest(){
        db.addReport(99, "Luba", 99);
    }

    @Test
    public void addReportDoctorDoesntExistTest(){
        db.addReport(111111111, "bla", 99);
    }

    @Test
    public void LubaAddReportCurrentTimestampTest(){
        Handler.printHeadline("Luba add Report Test");
        System.out.println("LocalDateTime now = LocalDateTime.now().minusMinutes(1);");
        LocalDateTime now = LocalDateTime.now().minusMinutes(1);
        System.out.println("now minus minutes: " + now + "\n");

        System.out.println("LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);");
        LocalDateTime nowEnd = LocalDateTime.now().plusMinutes(1);
        System.out.println("nowEnd plus minute: " + nowEnd + "\n");


        System.out.println("db.addReport(111111111,\"Luba\", 5);\n");
        db.addReport(111111111,"Luba", 5);


        System.out.println("List<Delay> delays = db.getReports(\"Luba\", now, nowEnd);\n");
        List<Delay> delays = db.getReports("Luba", now, nowEnd);

        System.out.println("Assert.assertEquals(1, delays.size());\n");
        Assert.assertEquals(1, delays.size());

        System.out.println("db.getDelayHandler(\"Luba\").deleteReports(now, nowEnd);\n" +
                "delays = db.getReports(\"Luba\", now, nowEnd);\n");
        db.getDelayHandler("Luba").deleteReports(now, nowEnd);
        delays = db.getReports("Luba", now, nowEnd);

        System.out.println("Assert.assertEquals(0, delays.size());");
        Assert.assertEquals(0, delays.size());

        Handler.printHeadline("Luba add Report Test current timestamp finished successfully");
    }


    @Test
    public void feedbackOnEstimateTest(){
        int userId = 443798604;
        int actualDelay = 35;
        String doctorsName = "Nordau";
        LocalDateTime appointmentTime = LocalDateTime.parse("2019-12-31 19:50:00", Entity.formatter);
        db.feedbackOnEstimate(userId, actualDelay);
        List<Delay> delays = db.getReports(doctorsName, appointmentTime, appointmentTime);
        Assert.assertEquals(1,delays.size());
        Delay delay = delays.get(0);
        System.out.println(delay.toString());
        Assert.assertEquals(35, delay.getReportedDelay());
        Assert.assertEquals(Delay.Type.FEEDBACK, delay.getReportType());
        Assert.assertEquals(appointmentTime, delay.getReportTimestamp());
        db.getDelayHandler(doctorsName).deleteReports(appointmentTime,appointmentTime);
        delays = db.getReports(doctorsName, appointmentTime, appointmentTime);
        Assert.assertEquals(0,delays.size());
    }

    @Test
    public void feedbackOnEstimateNotExistTest(){
        int userId = 99;
        db.feedbackOnEstimate(userId, userId);
    }

    /** the following test checks if all real doctors exist
     * and check if some not real doctors doesn't exist */
    @Test
    public void doctorExistsTest() {
        Handler.printHeadline("doctorExists Test");

        System.out.println("int realDoctorsCnt = countExistingDoctors(realDoctors);\n" +
                "Assert.assertEquals(realDoctors.length, realDoctorsCnt);\n");
        int realDoctorsCnt = countExistingDoctors(realDoctors);
        Assert.assertEquals(realDoctors.length, realDoctorsCnt);

        System.out.println("\nint notRealDoctorsCnt = countExistingDoctors(notRealDoctors);\n" +
                        "AAssert.assertEquals(0, notRealDoctorsCnt);\n");
        int notRealDoctorsCnt = countExistingDoctors(notRealDoctors);
        Assert.assertEquals(0, notRealDoctorsCnt);

        Handler.printHeadline("doctorExists Test finished successfully!");

    }

    private int countExistingDoctors(String[] doctorNames) {
        int existingDoctorCnt = 0;
        for(String doctor:doctorNames) {
            existingDoctorCnt = isDoctorExist(doctor) ? ++existingDoctorCnt : existingDoctorCnt;
        }
        return existingDoctorCnt;
    }

    private boolean isDoctorExist(String doctor_name){
        boolean b = db.doctorExists(doctor_name);
        if (b)
            System.out.println("Dr. " + doctor_name + " exists");
        else
            System.out.println("Dr. " + doctor_name + " doesn't exist");
        return b;
    }

    @Test
    public void getDoctorsTest(){
        Handler.printHeadline("Get Doctors test");
        System.out.println("List<Doctor> doctors = db.getDoctors();\n" +
                "Assert.assertEquals(realDoctors.length, doctors.size());\n" +
                "for(Doctor doctor: doctors) {\n" +
                "            Assert.assertNotNull(doctor);\n" +
                "            System.out.println(doctor.toString());\n" +
                "}");
        List<Doctor> doctors = db.getDoctors();
        Assert.assertEquals(realDoctors.length, doctors.size());
        for(Doctor doctor: doctors) {
            Assert.assertNotNull(doctor);
            System.out.println(doctor.toString());
        }
        Handler.printHeadline("Get Doctors test finished successfully");
    }

    @Test
    public void getDoctorTest(){
        Handler.printHeadline("Get Doctor test");
        System.out.println("for(String doctorsName:realDoctors){\n" +
                "            Doctor doctor = db.getDoctor(doctorsName);\n" +
                "            Assert.assertNotNull(doctor);\n" +
                "            System.out.println(doctor.toString());\n" +
                "}");
        for(String doctorsName:realDoctors){
            Doctor doctor = db.getDoctor(doctorsName);
            Assert.assertNotNull(doctor);
            System.out.println(doctor.toString());
        }

        System.out.println("for(String doctorsName:notRealDoctors){\n" +
                "            Doctor doctor = db.getDoctor(doctorsName);\n" +
                "            Assert.assertNull(doctor);\n" +
                "}");
        for(String doctorsName:notRealDoctors){
            Doctor doctor = db.getDoctor(doctorsName);
            Assert.assertNull(doctor);
        }
        Handler.printHeadline("Get Doctor test finished successfully");
    }

    @Test
    public void LubaGetReportsTest() {
        Handler.printHeadline("Luba get Reports Test");

        System.out.println("List<Delay> list = db.getReports(\"Luba\",Timestamp.valueOf(\"2019-01-01 07:00:00\").toLocalDateTime(), Timestamp.valueOf(\"2019-02-01 07:00:00\").toLocalDateTime());\n");
        List<Delay> list = db.getReports("Luba",Timestamp.valueOf("2019-01-01 07:00:00").toLocalDateTime(), Timestamp.valueOf("2019-02-01 07:00:00").toLocalDateTime());

        System.out.println("int num_of_reports = list.size();\n" +
                "Assert.assertEquals(num_of_reports, 2232);");
        int num_of_reports = list.size();
        Assert.assertEquals(num_of_reports, 2232);
        Handler.printHeadline("Luba Test finished successfully");
    }

    @Test
    public void MosheGetReportsTest() {
        Handler.printHeadline("Moshe get Reports Test");

        System.out.println("List<Delay> list = db.getReports(\"Moshe\",Timestamp.valueOf(\"2019-01-01 07:00:00\").toLocalDateTime(), Timestamp.valueOf(\"2019-02-01 07:00:00\").toLocalDateTime());\n");
        List<Delay> list = db.getReports("Moshe",Timestamp.valueOf("2019-01-01 07:00:00").toLocalDateTime(), Timestamp.valueOf("2019-02-01 07:00:00").toLocalDateTime());

        System.out.println("Assert.assertNull(list);");
        Assert.assertNull(list);

        Handler.printHeadline("Moshe Test finished successfully");
    }

    @Test
    public void getLubaDelaysTest(){
        Handler.printHeadline("get Luba Delays Test");
        System.out.println("List<Delay> delays = db.getDelays(\"Luba\");\n" +
                "Assert.assertEquals(26208, delays.size());\n");
        List<Delay> delays = db.getDelays("Luba");
        Assert.assertEquals(26208, delays.size());
        Handler.printHeadline("get Luba Delays Test finished successfully");
    }


    @Test
    public void getMosheDelaysTest(){
        Handler.printHeadline("get Moshe Delays Test");
        System.out.println("List<Delay> delays = db.getDelays(\"Moshe\");\n" +
                "Assert.assertNull(delays);\n");
        List<Delay> delays = db.getDelays("Moshe");
        Assert.assertNull(delays);
        Handler.printHeadline("get Moshe Delays Test finished successfully");
    }

    @Test
    public void LubaGetDayReportTest() {
        Handler.printHeadline("LubaGetDayReportTest");

        System.out.println("List<Delay> list = db.getDayReport(\"Luba\", LocalDate.parse(\"2019-01-01\"));\n");
        List<Delay> list = db.getDayReport("Luba", LocalDate.parse("2019-01-01"));

        System.out.println("int num_of_reports = list.size();\n" +
                "Assert.assertEquals(num_of_reports, 72);");
        int num_of_reports = list.size();
        Assert.assertEquals(num_of_reports, 72);
        Handler.printHeadline("LubaGetDayReportTest finished successfully");
    }

    @Test
    public void MosheGetDayReportTest() {
        Handler.printHeadline("MosheGetDayReportTest");

        System.out.println("List<Delay> list = db.getDayReport(\"Moshe\", LocalDate.parse(\"2019-01-01\"));\n");
        List<Delay> list = db.getDayReport("Moshe", LocalDate.parse("2019-01-01"));

        System.out.println("\nAssert.assertNull(list);\n");
        Assert.assertNull(list);
        Handler.printHeadline("MosheGetDayReportTest finished successfully");
    }

    @Test
    public void printTable(){
        db.printTable("Nordau");
    }


    @Test
    public void printNotExistingTable(){
        db.printTable("bla");
    }

    @Test
    public void testPath(){
        String userDir = System.getProperty("user.dir");
        String substring = userDir.length() > 2 ? userDir.substring(userDir.length() - 2) : null;
        if (substring != null){
            System.out.println(substring);
        }
        System.out.println(substring.equals("DB"));
    }

    @Test
    public void TestAllDoctors(){
        Handler.printHeadline("TestAllDoctors");
        List<Doctor> doctors = db.getDoctors();
        for (Doctor doctor: doctors){
            System.out.println(doctor.toString());
            Assert.assertTrue(db.doctorExists(doctor.getName()));
            Doctor getDoctor = db.getDoctor(doctor.getName());
            System.out.println("Assert.assertEquals(doctor.getName(), getDoctor.getName());\n" +
                    "Assert.assertEquals(doctor.getEndTime(), getDoctor.getEndTime());\n" +
                    "Assert.assertEquals(doctor.getInterval(), getDoctor.getInterval());\n" +
                    "Assert.assertEquals(doctor.getStartTime(), getDoctor.getStartTime());\n" +
                    "Assert.assertEquals(doctor.getType(), getDoctor.getType());");
            Assert.assertEquals(doctor.getName(), getDoctor.getName());
            Assert.assertEquals(doctor.getEndTime(), getDoctor.getEndTime());
            Assert.assertEquals(doctor.getInterval(), getDoctor.getInterval());
            Assert.assertEquals(doctor.getStartTime(), getDoctor.getStartTime());
            Assert.assertEquals(doctor.getType(), getDoctor.getType());
            int numOfDelaysBeforeAddition = db.getDelays(doctor.getName()).size();
            db.addReport(111111111, doctor.getName(), 30);
            int numOfDelaysAfterAddition = db.getDelays(doctor.getName()).size();
            System.out.println("Assert.assertEquals(numOfDelaysBeforeAddition +1, numOfDelaysAfterAddition);");
            Assert.assertEquals(numOfDelaysBeforeAddition +1, numOfDelaysAfterAddition);
            db.getDelayHandler(doctor.getName()).deleteReports(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
            Appointment lastAppointment = db.getLastAppointment(111111111);
            if (lastAppointment != null && lastAppointment.getDoctorsName().equals(doctor.getName())){
                System.out.println(lastAppointment.toString());
                int numOfReportsBeforeAddition = db.getReports(doctor.getName(), lastAppointment.getAppointmentDateTime(), lastAppointment.getAppointmentDateTime()).size();
                db.feedbackOnEstimate(111111111, 10);
                int numOfReportsAfterAddition = db.getReports(doctor.getName(), lastAppointment.getAppointmentDateTime(), lastAppointment.getAppointmentDateTime()).size();
                System.out.println("Assert.assertEquals(numOfReportsBeforeAddition +1, numOfReportsAfterAddition);");
                Assert.assertEquals(numOfReportsBeforeAddition +1, numOfReportsAfterAddition);
                db.getDelayHandler(doctor.getName()).deleteReports(lastAppointment.getAppointmentDateTime(), lastAppointment.getAppointmentDateTime());
            }
        }
        Handler.printHeadline("TestAllDoctors finished");

    }

}
