package EntityTest;

import entities.Entity;
import entities.Patient;
import handlers.Handler;
import org.junit.Assert;
import org.junit.Test;

public class PatientTest {

    @Test
    public void compactPatientTest(){
        Handler.printHeadline("compact Patient Test");
        System.out.println("String personalId = \"123456789\";\n" +
                "String password = \"123456789\";\n" +
                "Patient patient = new Patient(personalId,password);\n" +
                "System.out.println(patient.toString());\n");
        String personalId = "123456789";
        String password = "123456789";
        Patient patient = new Patient(personalId,password);
        System.out.println(patient.toString());
        System.out.println("Assert.assertEquals(personalId, patient.getPersonalId());\n" +
                "Assert.assertEquals(password, patient.getPassword());\n" +
                "Assert.assertEquals(0, patient.getScore());\n" +
                "Assert.assertEquals(Patient.Type.USER, patient.getReportType());");
        Assert.assertEquals(personalId, patient.getPersonalId());
        Assert.assertEquals(password, patient.getPassword());
        Assert.assertEquals(0, patient.getScore());
        Assert.assertEquals(Patient.Type.USER, patient.getReportType());
        System.out.println("int addedScore = 3;\n" +
                "patient.addScore(addedScore);\n" +
                "System.out.println(patient.toString());\n");
        int addedScore = 3;
        patient.addScore(addedScore);
        System.out.println(patient.toString());
        System.out.println("Assert.assertEquals(score + addedScore, patient.getScore());");
        Assert.assertEquals(addedScore, patient.getScore());
        Handler.printHeadline("Patient compact Test finished successfully");

    }


    @Test
    public void patientTest(){
        Handler.printHeadline("Patient Test");
        System.out.println("String personalId = \"123456789\";\n" +
                "String password = \"123456789\";\n" +
                "int score = 1;\n" +
                "Entity.Type type = Entity.Type.EXPERT;\n" +
                "Patient patient = new Patient(personalId,password,score, type);\n" +
                "System.out.println(patient.toString());\n");
        String personalId = "123456789";
        String password = "123456789";
        int score = 1;
        Entity.Type type = Entity.Type.EXPERT;
        Patient patient = new Patient(personalId,password,score, type);
        System.out.println(patient.toString());
        System.out.println("Assert.assertEquals(personalId, patient.getPersonalId());\n" +
                "Assert.assertEquals(password, patient.getPassword());\n" +
                "Assert.assertEquals(score, patient.getScore());\n" +
                "Assert.assertEquals(type, patient.getReportType());");
        Assert.assertEquals(personalId, patient.getPersonalId());
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
        Handler.printHeadline("Patient Test finished successfully");

    }
}
