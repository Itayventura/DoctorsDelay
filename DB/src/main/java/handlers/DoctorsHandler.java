package handlers;

import entities.Doctor;
import repository.DoctorsRepository;

import java.util.List;

public class DoctorsHandler extends Handler {
    private static final String sqlSelectAll = "Select * from doctors ";
    private static final String sqlDelete = "Delete from doctors Where doctor_name = '";
    private static final String sqlInsert = "INSERT INTO doctors(doctor_name) VALUES('";
    private static final String sqlDeleteTestDoctors = "delete from  doctors where doctor_name LIKE '%" + "testDoctor" + "%'";
    private static final int DOCTORS_ATTRIBUTES = 5;

    public DoctorsHandler(){
        repository = new DoctorsRepository();
    }

    public void addDoctor(String doctorsName) {
        String sqlInsertDoctor = sqlInsert + doctorsName + "')";
        repository.insert(sqlInsertDoctor);
    }

    public void removeDoctor(String doctorsName){
        String sqlDeleteDoctor = sqlDelete + doctorsName + "'";
        repository.delete(sqlDeleteDoctor);
    }

    /**
     * @param doctorsName name of the doctor
     * @return null if doctor doesnt exist
     */
    public Doctor getDoctor(String doctorsName){
        String sqlDoctor = sqlSelectAll + "WHERE doctor_name = '" + doctorsName + "'";
        List<Doctor> doctors = repository.select(sqlDoctor);
        return doctors.isEmpty()? null:doctors.get(0);
    }

    public int getNumberOfDoctors() {
        return getDoctors().size();
    }

    public List<Doctor> getDoctors() {
        return repository.select(sqlSelectAll);
    }

        public void printTable(){
        printHeadline("DOCTORS", DOCTORS_ATTRIBUTES);
        printTable(sqlSelectAll);
    }


//todo are these methods relevant?
    public void deleteTestDoctors() {
        repository.delete(sqlDeleteTestDoctors);
    }
}
