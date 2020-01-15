package handlers;

import entities.Doctor;
import repository.DoctorsRepository;

import java.util.List;

public class DoctorsHandler extends Handler {
    private static final String sqlSelectAll = "Select * from doctors ";
    private static final String sqlDelete = "Delete from doctors Where doctor_name = '";
    private static final String sqlInsert = "INSERT INTO doctors(doctor_name) VALUES('";
    private static final int DOCTORS_ATTRIBUTES = 5;

    public DoctorsHandler(){
        repository = new DoctorsRepository();
    }

    public void addDoctor(String doctorsName) {
        createTable(doctorsName);
        String sqlInsertDoctor = sqlInsert + doctorsName + "')";
        repository.insert(sqlInsertDoctor);
    }


    private void createTable(String doctorsName) {
        String table = String.format("`%s`", doctorsName.replace("`", "``"));
        String sqlCreate = "CREATE TABLE " + table + " (\n" +
                "\t`report_id` INT(9) NOT NULL AUTO_INCREMENT,\n" +
                "\t`reported_delay_minutes` INT(11) NOT NULL DEFAULT '0',\n" +
                "\t`timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "\t`report_type` ENUM('user','feedback','expert') NOT NULL DEFAULT 'user',\n" +
                "\t`patient_id` VARCHAR(9) NOT NULL DEFAULT '111111111',\n" +
                "\tPRIMARY KEY (`report_id`),\n" +
                "\tINDEX `TIMESTAMP KEY` (`timestamp`),\n" +
                "\tCONSTRAINT " + table +" FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`)\n" +
                ")\n" +
                "ENGINE=InnoDB\n" +
                "AUTO_INCREMENT=0";
        repository.update(sqlCreate);
    }

    public void removeDoctor(String doctorsName){
        dropTable(doctorsName);
        String sqlDeleteDoctor = sqlDelete + doctorsName + "'";
        repository.delete(sqlDeleteDoctor);
    }

    private void dropTable(String doctorsName) {
        String table = String.format("`%s`", doctorsName.replace("`", "``"));
        String sqlDrop = "DROP TABLE " + table;
        repository.update(sqlDrop);
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

}
