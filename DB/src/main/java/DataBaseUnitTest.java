import org.junit.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

public class DataBaseUnitTest {

    private DataBase db;
    private static String[] realDoctors = {"Itay Ventura",
                                            "Bar Gal",
                                            "Lior Carmon",
                                            "Michal Deutch",
                                            "Ron Mor",
                                            "Shachar Zilbershtien",
                                            "Guli Gool",
                                            "Guli Goool",
                                            "Guli Gooool"};

    private static String[] notRealDoctors = { "Ientura",
                                                "Idfgra",
                                                "Ron Ban",
                                                "Gal Gal",
                                                "Itay Mor"};
    private static int num_of_doctor_tests = 10;
    // todo protected static final Logger logger = Logger.getLogger(String.valueOf(DataBaseUnitTest.class));

    public DataBaseUnitTest(DataBase dataBase)
    {
        DataBaseImpl.printHeadline("DB Test");
        db = dataBase;

        doctorExistsTest();
        addReportTest();
        getReportsTest();
        DayReportTest();

    }

    private void DayReportTest() {

    }

    private void getReportsTest() {
        //todo not enough tests! check syntax. and testDoctor.
        DataBaseImpl.printHeadline("getReports Test");


        List<DataBase.DelayReport> list = db.getReports("Itay Ventura",
                Timestamp.valueOf("2019-11-26 10:40:32"),
                Timestamp.valueOf("2019-11-26 13:40:33"));
        int num_of_reports = list.size();
        try {
            Assert.assertEquals(num_of_reports, 13);
        }catch (AssertionError ae) {
            String errorMessage = "num_of_reports is " + num_of_reports + "\n"
                    + "and it should be: " + 13 + "\n";
            DataBaseImpl.logger.error(errorMessage, ae);
            throw new RuntimeException("errorMessage", ae);
        }

       // DataBaseImpl.printReportsFromList(list);

        DataBaseImpl.printHeadline("getReports Test finished successfully!");
    }

    private void addReportTest(){
        DataBaseImpl.printHeadline("addReport Test");
        String[] testDoctor = new String[num_of_doctor_tests];

        addTestReports(testDoctor);

        doctorsTest(testDoctor, true);
        reportsTest(testDoctor, true);

        printTestReports(testDoctor);

        deleteTestReports();
        deleteTestDoctors();

        doctorsTest(testDoctor, false);
        reportsTest(testDoctor, false);

        DataBaseImpl.printHeadline("addReport Test finished successfully!");
    }

    private void reportsTest(String[] testDoctor, boolean beforeDeletion) {
        DataBaseImpl.printHeadline("reportsTest");
        int num_of_reports = 0;
        try {
            ResultSet ResultSetNumOfReports = SQLQueryImpl.getNumOfTestDoctorReports();
            ResultSetNumOfReports.next();
            num_of_reports = ResultSetNumOfReports.getInt("num_of_reports");
            assertReportsTest(num_of_reports, beforeDeletion);
        }catch (SQLException e) {
            String errorMessage = "reportsTest SQL Exception\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    private void assertReportsTest(int num_of_reports, boolean beforeDeletion) {
        if(beforeDeletion) {
            try {
                Assert.assertEquals(num_of_reports, num_of_doctor_tests * 3);
            } catch (AssertionError ae) {
                String errorMessage = "before deletion num_of_reports is " + num_of_reports + "\n"
                        + "TestDoctorReports real is " + num_of_doctor_tests * 3 + "\n";
                DataBaseImpl.logger.error(errorMessage, ae);
                throw new RuntimeException("errorMessage", ae);
            }
        }
        else{
            try {
                Assert.assertEquals(num_of_reports, 0);
            }catch (AssertionError ae) {
                String errorMessage = "after deletion num_of_reports is " + num_of_reports + "\n"
                        + "TestDoctorReports real is " + 0 + "\n";
                DataBaseImpl.logger.error(errorMessage, ae);
                throw new RuntimeException("errorMessage", ae);
            }
        }
    }

    private void doctorsTest(String[] testDoctor, boolean beforeDeletion) {

        DataBaseImpl.printHeadline("doctorsTest");
        int testDoctorCnt = countExistingDoctors(testDoctor);

        if(beforeDeletion) {
            try {
                Assert.assertEquals(testDoctorCnt, testDoctor.length);
            } catch (AssertionError e) {
                String errorMessage = "testDoctorCnt is " + testDoctorCnt + "\n"
                        + "testDoctor.length is " + testDoctor.length + "\n";
                DataBaseImpl.logger.error(errorMessage, e);
                throw new RuntimeException("errorMessage", e);
            }
        }
        else{
            try {
                Assert.assertEquals(testDoctorCnt, 0);
            } catch (AssertionError e) {
                String errorMessage = "testDoctorCnt is " + testDoctorCnt + "\n"
                        + "number of doctors after deletion is " + 0 + "\n";
                DataBaseImpl.logger.error(errorMessage, e);
                throw new RuntimeException("errorMessage", e);
            }
        }
    }

    private void deleteTestReports() {
        DataBaseImpl.printHeadline("deleteTestReports");
        SQLQueryImpl.deleteTestReports();
    }

    private void deleteTestDoctors() {
        DataBaseImpl.printHeadline("deleteTestDoctors");
        SQLQueryImpl.deleteTestDoctors();
    }

    private void printTestReports(String[] testDoctor) {
        DataBaseImpl.printHeadline("printTestReports");
        for(String doctor:testDoctor){
            DataBaseImpl.printHeadline(doctor);

            List<DataBase.DelayReport>  testDoctorReports = db.getReports(doctor,
                                                                        Timestamp.valueOf("2019-12-05 00:19:56"), //todo make it global
                                                                        Timestamp.valueOf("2019-12-05 23:50:00")); //todo make it global
            DataBaseImpl.printReportsFromList(testDoctorReports);
        }
    }

    private void addTestReports(String[] testDoctor) {
        DataBaseImpl.printHeadline("addTestReports");
        for(int i =0; i < num_of_doctor_tests; i++) {
            String doctor_name = "testDoctor" + i;
            db.addReport(doctor_name, i);
            db.addReport(doctor_name, 2*i);
            db.addReport(doctor_name, 3*i);
            testDoctor[i] = doctor_name;
        }

    }

    private void doctorExistsTest() {
        DataBaseImpl.printHeadline("doctorExists Test");

        int realDoctorsCnt = 0;
        int notRealDoctorsCnt = 0;

        try{
            realDoctorsCnt = countExistingDoctors(realDoctors); // todo method
            Assert.assertEquals(realDoctorsCnt, SQLQueryImpl.getNumberOfDoctors());

            notRealDoctorsCnt = countExistingDoctors(notRealDoctors);
            Assert.assertEquals(notRealDoctorsCnt, 0);

            DataBaseImpl.printHeadline("doctorExists Test finished successfully!");

        }catch (AssertionError e) {
            String errorMessage = "number of real Dr. is " + SQLQueryImpl.getNumberOfDoctors() + "\n"
                                + "DB Test number of real Dr. is " + realDoctorsCnt + "\n"
                                + "DB Test number of not real Dr. is " + notRealDoctorsCnt + "\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);

        }
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

}
