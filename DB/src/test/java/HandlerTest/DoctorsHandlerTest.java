package HandlerTest;

import db.DataBaseImpl;
import entities.Doctor;
import handlers.DoctorsHandler;
import handlers.Handler;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DoctorsHandlerTest {

    static {
        DataBaseImpl.init();
    }

    static class GetDoctor extends Thread {
        private String doctorsName;
        private Doctor doctor;
        GetDoctor(String doctorsName) { this.doctorsName = doctorsName;}
        public void run() {
            doctor = doctorsHandler.getDoctor(doctorsName);
        }

         Doctor getDoctor(){ return doctor;}
    }

    private static DoctorsHandler doctorsHandler = new DoctorsHandler();
    private static List<Doctor> doctors = doctorsHandler.getDoctors();


    @Test
    public void GetDoctorTest() throws InterruptedException {
        List<GetDoctor> threads = new ArrayList<>();
        for (Doctor doctor: doctors){
            threads.add(new GetDoctor(doctor.getName()));
        }
        for (Thread thread: threads)
            thread.start();

        for (Thread thread: threads)
            thread.join();

        for (int i = 0; i < doctors.size(); i++){
            Assert.assertEquals(doctors.get(i).getName(), threads.get(i).getDoctor().getName());
        }
    }

    /** the following test invokes all methods in DoctorsHandler */
    @Test
    public void addDoctorTest(){
        Handler.printHeadline("Add Doctor Test");
        Doctor doctor;

        System.out.println("int numOfDoctorsBeforeAddition = doctorsHandler.getNumberOfDoctors();");
        int numOfDoctorsBeforeAddition = doctorsHandler.getNumberOfDoctors();
        System.out.println("number of doctors before addition: " + numOfDoctorsBeforeAddition + "\n");

        System.out.println("doctorsHandler.addDoctor(testDoctor);");
        String testDoctor = "Test";
        doctorsHandler.addDoctor(testDoctor);

        System.out.println("int numOfDoctorsAfterAddition = doctorsHandler.getNumberOfDoctors();");
        int numOfDoctorsAfterAddition = doctorsHandler.getNumberOfDoctors();
        System.out.println("number of doctors after addition: " + numOfDoctorsAfterAddition + "\n");

        System.out.println("Assert.assertEquals(numOfDoctorsAfterAddition, numOfDoctorsBeforeAddition + 1);\n");
        Assert.assertEquals(numOfDoctorsAfterAddition, numOfDoctorsBeforeAddition + 1);

        System.out.println("doctor = doctorsHandler.getDoctor(testDoctor);\n" +
                            "System.out.println(doctor.toString());\n");
        doctor = doctorsHandler.getDoctor(testDoctor);
        System.out.println(doctor.toString());

        System.out.println("doctorsHandler.removeDoctor(testDoctor);");
        doctorsHandler.removeDoctor(testDoctor);

        System.out.println("int numOfDoctorsAfterDeletion = doctorsHandler.getNumberOfDoctors();");
        int numOfDoctorsAfterDeletion = doctorsHandler.getNumberOfDoctors();
        System.out.println("number of doctors after deletion: " + numOfDoctorsAfterDeletion + "\n");

        System.out.println("Assert.assertEquals(numOfDoctorsBeforeAddition, numOfDoctorsAfterDeletion);");
        Assert.assertEquals(numOfDoctorsBeforeAddition, numOfDoctorsAfterDeletion);

        Handler.printHeadline("Add Doctor Test finished successfully");
    }

    @Ignore("Used to get information")
    @Test
    public void printTable(){
        System.out.println("doctorsHandler.printTable();");
        doctorsHandler.printTable();
    }
}
