package EntityTest;

import entities.Entity;
import entities.Patient;
//import handlers.Handler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PatientTest {

    @BeforeClass
    public static void setUpClass(){
        //Handler.printHeadline("Patient Test");
    }


    @Test
    public void patientTest(){
        System.out.println("String personalId = \"123456789\";\n" +
                "String password = \"123456789\";\n" +
                "int score = 0;\n" +
                "Entity.Type type = Entity.Type.USER;\n" +
                "Patient patient = new Patient(personalId,password,score, type);\n" +
                "System.out.println(patient.toString());\n");
        String personalId = "123456789";
        String password = "123456789";
        int score = 0;
        Entity.Type type = Entity.Type.USER;
        Patient patient = new Patient(personalId,password,score, type);
        System.out.println(patient.toString());
        System.out.println("Assert.assertEquals(score, patient.getScore());\n" +
                "Assert.assertEquals(password, patient.getPassword());\n" +
                "Assert.assertEquals(score, patient.getScore());\n" +
                "Assert.assertEquals(type, patient.getReportType());");
        Assert.assertEquals(score, patient.getScore());
        Assert.assertEquals(password, patient.getPassword());
        Assert.assertEquals(score, patient.getScore());
        Assert.assertEquals(type, patient.getReportType());
        System.out.println("int addedScore = 3;\n" +
                "patient.addScore(addedScore);\n" +
                "System.out.println(patient.toString());\n");
        int addedScore = 3;
        patient.addScore(addedScore);
        System.out.println(patient.toString());
        System.out.println("Assert.assertEquals(score + addedScore, patient.getScore());");
        Assert.assertEquals(score + addedScore, patient.getScore());
        //Handler.printHeadline("Patient Test finished successfully");

    }
}
