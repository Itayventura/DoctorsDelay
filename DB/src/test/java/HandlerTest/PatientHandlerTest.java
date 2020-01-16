package HandlerTest;

import db.DataBaseImpl;
import entities.Patient;
import handlers.Handler;
import handlers.PatientsHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PatientHandlerTest {

    static {
        DataBaseImpl.init();
    }

    static class GetPatient extends Thread {
        private int patientId;
        Patient patient;
        GetPatient(int patientId) {
            this.patientId = patientId;
        }
        public void run() {
            patient = patientsHandler.getPatient(patientId);
        }

        Patient getPatient() {
            return patient;
        }
    }

    static class AddScore extends Thread {
        private int patientId;
        private int scoreAdded;
        AddScore(int patientId, int scoreAdded) {
            this.patientId = patientId;
            this.scoreAdded = scoreAdded;
        }
        public void run() {
             patientsHandler.addScore(patientId, scoreAdded);
        }
    }

    private static PatientsHandler patientsHandler = new PatientsHandler();
    private static List<Patient> patients = patientsHandler.getPatients();

    static int[] realPatients = new int[]{ 111111111,
                                            123123123,
                                            123456789
                                        };
    static int[] notRealPatients = new int[]{0,
                                             2,
                                             };
    private static Patient patient;

    @BeforeClass
    public static void setUpClass(){
        Handler.printHeadline("set Up Class PatientsHandlerTest");



        System.out.println("Patient patient = patientsHandler.getPatient(111111111);\n" +
                "System.out.println(patient.toString());\n");
        patient = patientsHandler.getPatient(111111111);
        System.out.println(patient.toString());
    }

    @Test
    public void AddScoreGetPatientTest() throws InterruptedException {
        int num_of_threads = 100;

        GetPatient[] GetPatientThreads = new GetPatient[num_of_threads];
        AddScore[] AddScoreThreads = new AddScore[num_of_threads];

        int[] scoreBeforeAddition = new int[num_of_threads];
        int[] scoreAfterAddition = new int[num_of_threads];

        for(int i = 0; i < num_of_threads; i++) {
            int patientId = Integer.parseInt(patients.get(i).getPersonalId());
            GetPatientThreads[i] = new GetPatient(patientId);
            AddScoreThreads[i] = new AddScore(patientId, 3);
        }
        for (Thread T: GetPatientThreads)
            T.start();
        for (Thread T: GetPatientThreads)
            T.join();
        for (int i = 0; i < num_of_threads; i++)
            scoreBeforeAddition[i] = GetPatientThreads[i].getPatient().getScore();

        for (Thread T: AddScoreThreads)
            T.start();
        for (Thread T: AddScoreThreads)
            T.join();

        for(int i = 0; i < num_of_threads; i++) {
            int patientId = Integer.parseInt(patients.get(i).getPersonalId());
            GetPatientThreads[i] = new GetPatient(patientId);
        }
        for (Thread T: GetPatientThreads)
            T.start();
        for (Thread T: GetPatientThreads)
            T.join();

        for (int i = 0; i < num_of_threads; i++)
            scoreAfterAddition[i] = GetPatientThreads[i].getPatient().getScore();

        for (int i = 0; i < num_of_threads; i++)
            Assert.assertEquals(scoreBeforeAddition[i] + 3 ,scoreAfterAddition[i]);
    }

    @Test
    public void addScoreTest(){
        Handler.printHeadline("Add Score Test");

        System.out.println("int patientScore = patient.getScore();\n" +
                           "int scoreAdded = 3;\n");
        int patientScore = patient.getScore();
        int scoreAdded = 3;

        System.out.println("patientsHandler.addScore(111111111,scoreAdded);\n" +
                "patient = patientsHandler.getPatient(111111111);\n" +
                "System.out.println(patient.toString());\n");
        patientsHandler.addScore(111111111,scoreAdded);
        patient = patientsHandler.getPatient(111111111);
        System.out.println(patient.toString());

        System.out.println("int patientScoreAfterAddition = patient.getScore();\n");
        int patientScoreAfterAddition = patient.getScore();

        System.out.println("Assert.assertEquals(patientScoreAfterAddition, patientScore + scoreAdded);");
        Assert.assertEquals(patientScoreAfterAddition, patientScore + scoreAdded);

        Handler.printHeadline("Add Score Test finished successfully");
    }


    @Test
    public void addScoreTest_notExistingPatient(){
        Handler.printHeadline("addScoreTest_notExistingPatient");

        System.out.println("int notExistingPatientId = notRealPatients[0];");
        int notExistingPatientId = notRealPatients[0];

        System.out.println("Assert.assertFalse(patientsHandler.patientExists(notExistingPatientId));\n" +
                "int scoreAdded = 3;\n");
        Assert.assertFalse(patientsHandler.patientExists(notExistingPatientId));
        int scoreAdded = 3;

        System.out.println("patientsHandler.addScore(notExistingPatientId,scoreAdded);\n" +
                "Assert.assertFalse(patientsHandler.patientExists(notExistingPatientId));");
        patientsHandler.addScore(notExistingPatientId,scoreAdded);
        Assert.assertFalse(patientsHandler.patientExists(notExistingPatientId));

        System.out.println("\nif we have reached so far\n" +
                "patient " + notExistingPatientId + " does not exist\n"
                + "and therefore, score was not added to patient " + notExistingPatientId);

        Handler.printHeadline("addScoreTest_notExistingPatient finished successfully");
    }

    @Test
    public void getUserPasswordTest(){
        Handler.printHeadline("getUserPasswordTest");

        System.out.println("String password = patient.getPassword();\n" +
                "Assert.assertEquals(\"111111111\",password);");
        String password = patient.getPassword();
        Assert.assertEquals("111111111",password);

        System.out.println("password = patientsHandler.getUserPassword(111111111);\n" +
                "Assert.assertEquals(\"111111111\",password);");
        password = patientsHandler.getUserPassword(111111111);
        Assert.assertEquals("111111111",password);

        Handler.printHeadline("getUserPasswordTest finished successfully");
    }

    @Test
    public void getUserPasswordTest_notExistingPatient(){
        Handler.printHeadline("getUserPasswordTest_notExistingPatient");

        System.out.println("Patient patientNotExisting = patientsHandler.getPatient(0);\n" +
                "Assert.assertNull(\"found not existing patient\", patientNotExisting);\n" +
                "String password = patientsHandler.getUserPassword(0);\n" +
                "Assert.assertNull(\"found not existing patient\", password);");
        Patient patientNotExisting = patientsHandler.getPatient(0);
        Assert.assertNull("found not existing patient", patientNotExisting);
        String password = patientsHandler.getUserPassword(0);
        Assert.assertNull("found not existing patient", password);

        Handler.printHeadline("getUserPasswordTest_notExistingPatient finished successfully");
    }


    /** the following test checks if all real patients exist
     * and check if some not real patients doesn't exist */
    @Test
    public void patientExistsTest() {
        Handler.printHeadline("patientExists Test");

        System.out.println("int realPatientsCnt = countExistingPatients(realPatients);\n" +
                "Assert.assertEquals(realPatientsCnt, realPatients.length);\n");
        int realPatientsCnt = countExistingPatients(realPatients);
        Assert.assertEquals(realPatientsCnt, realPatients.length);

        System.out.println("\nint notRealPatientsCnt = countExistingPatients(notRealPatients);\n" +
                "Assert.assertEquals(notRealPatientsCnt, 0);\n");
        int notRealPatientsCnt = countExistingPatients(notRealPatients);
        Assert.assertEquals(notRealPatientsCnt, 0);

        Handler.printHeadline("doctorExists Test finished successfully!");

    }

    private int countExistingPatients(int[] patientsIds) {
        int existingPatientCnt = 0;
        for(int patientId:patientsIds) {
            existingPatientCnt = isPatientExist(patientId) ? ++existingPatientCnt : existingPatientCnt;
        }
        return existingPatientCnt;
    }

    private boolean isPatientExist(int patientId){
        boolean b = patientsHandler.patientExists(patientId);
        if (b)
            System.out.println("Patient " + patientId + " exists");
        else
            System.out.println("Patient " + patientId + " doesn't exist");
        return b;
    }

    @Test
    public void addPatientsTest(){
        Handler.printHeadline("Add Patients Test");


        System.out.println("Assert.assertFalse(\"patient exists before addition\",patientsHandler.patientExists(98));\n");
        Assert.assertFalse("patient exists before addition\n",patientsHandler.patientExists(98));

        System.out.println("Assert.assertFalse(\"patient exists before addition\",patientsHandler.patientExists(99));\n");
        Assert.assertFalse("patient exists before addition\n",patientsHandler.patientExists(99));

        System.out.println("int numOfPatientsBeforeAddition = patientsHandler.getPatients().size();\n");
        int numOfPatientsBeforeAddition = patientsHandler.getPatients().size();

        System.out.println("List<Patient> patients = new ArrayList<>();\n" +
                "patients.add(new Patient(\"98\", \"98\"));\n" +
                "patients.add(new Patient(\"99\", \"99\"));\n");
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("98", "98"));
        patients.add(new Patient("99", "99"));

        System.out.println("patientsHandler.addPatients(patients);\n");
        patientsHandler.addPatients(patients);

        System.out.println("int numOfPatientsAfterAddition = patientsHandler.getPatients().size();\n" +
                "Assert.assertEquals(numOfPatientsBeforeAddition + 2, numOfPatientsAfterAddition);\n");
        int numOfPatientsAfterAddition = patientsHandler.getPatients().size();
        Assert.assertEquals(numOfPatientsBeforeAddition + 2, numOfPatientsAfterAddition);

        System.out.println("Assert.assertTrue(\"patient doesn't exist after addition\",patientsHandler.patientExists(98));\n");
        Assert.assertTrue("patient doesn't exist after addition", patientsHandler.patientExists(98));

        System.out.println("Assert.assertTrue(\"patient doesn't exist after addition\",patientsHandler.patientExists(99));\n");
        Assert.assertTrue("patient doesn't exist after addition", patientsHandler.patientExists(99));

        System.out.println("Patient patientAdded = patientsHandler.getPatient(98);\n" +
                "Assert.assertEquals(\"98\", patientAdded.getPersonalId());\n" +
                "Assert.assertEquals(\"98\", patientAdded.getPassword());\n" +
                "System.out.println(patientAdded.toString());\n");
        Patient patientAdded = patientsHandler.getPatient(98);
        Assert.assertEquals("98", patientAdded.getPersonalId());
        Assert.assertEquals("98", patientAdded.getPassword());
        System.out.println(patientAdded.toString());

        System.out.println("patientAdded = patientsHandler.getPatient(99);\n" +
                "Assert.assertEquals(\"99\", patientAdded.getPersonalId());\n" +
                "Assert.assertEquals(\"99\", patientAdded.getPassword());\n" +
                "System.out.println(patientAdded.toString());\n");
        patientAdded = patientsHandler.getPatient(99);
        Assert.assertEquals("99", patientAdded.getPersonalId());
        Assert.assertEquals("99", patientAdded.getPassword());
        System.out.println(patientAdded.toString());

        System.out.println("patientsHandler.removePatient(98);\n");
        patientsHandler.removePatient(98);

        System.out.println("patientsHandler.removePatient(99);\n");
        patientsHandler.removePatient(99);

        System.out.println("Assert.assertFalse(\"patient exists after deletion\", patientsHandler.patientExists(98));\n");
        Assert.assertFalse("patient exists after deletion", patientsHandler.patientExists(98));

        System.out.println("Assert.assertFalse(\"patient exists after deletion\", patientsHandler.patientExists(99));\n");
        Assert.assertFalse("patient exists after deletion", patientsHandler.patientExists(99));

        System.out.println("int numOfPatientsAfterDeletion = patientsHandler.getPatients().size();\n" +
                "Assert.assertEquals(numOfPatientsBeforeAddition, numOfPatientsAfterDeletion);\n");
        int numOfPatientsAfterDeletion = patientsHandler.getPatients().size();
        Assert.assertEquals(numOfPatientsBeforeAddition, numOfPatientsAfterDeletion);


        Handler.printHeadline("Add Patient Test finished successfully");
    }

    @Test
    public void printTable(){
        patientsHandler.printTable();
    }

    @Test
    public void test(){
        System.out.println(patientsHandler.patientExists(949962296));
        Patient patient = patientsHandler.getPatient(949962296);
        System.out.println(patient.toString());
    }

}
