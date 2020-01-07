package handlers;

import entities.Patient;
import repository.PatientsRepository;
import java.util.List;

public class PatientsHandler extends Handler {

    private static final int PATIENTS_ATTRIBUTES = 4;
    private static final String sqlSelectAll = "SELECT * FROM patients ";
    private static final String sqlSetScore = "UPDATE patients SET score = '";
    private static final String sqlDelete = "Delete from patients Where patient_id = '";
    private static final String sqlInsert = "INSERT INTO patients(patient_id, password) VALUES";

    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PatientsHandler.class);

    public PatientsHandler(){
        repository = new PatientsRepository();
    }

    public void addScore(int userId, int scoreAdded) {
        Patient patient = getPatient(userId);
        int newScore;
        if (patient != null){
            newScore = patient.getScore() + scoreAdded;
            String sqlAddScore = sqlSetScore + newScore + "' WHERE patient_id = '" + userId + "'";
            repository.update(sqlAddScore);
        }
        else
            logger.info("patient " + userId + " doesn't exist");
    }

    public Patient getPatient(int userId) {
        String sqlGetPatient =  sqlSelectAll + "WHERE patient_id = '" + userId + "'";
        List<Patient> patient = repository.select(sqlGetPatient);
        return patient.isEmpty()? null:patient.get(0);
    }

    public List<Patient> getPatients(){
        return repository.select(sqlSelectAll);
    }

    public boolean patientExists(int userId){
        return getPatient(userId) != null;
    }


    public String getUserPassword(int userId) {
        Patient patient = getPatient(userId);
        if (patient == null) {
            logger.info("patient " + userId + " doesn't exist");
            return null;
        }
        return patient.getPassword();
    }

    public int getScore(int userId){
        Patient patient = getPatient(userId);
        if (patient == null) {
            logger.info("patient " + userId + " doesn't exist");
            return -1;
        }
        return patient.getScore();
    }

    public void addPatients(List<Patient> patients) {
        String sqlInsertPatients = sqlInsert;
        for (int i = 0; i < patients.size(); i++) {
            Patient patient = patients.get(i);
            String sqlValues = "('" + patient.getPersonalId() + "','" + patient.getPassword() + "')";
            sqlInsertPatients += sqlValues;
            if (i != patients.size() - 1)
                sqlInsertPatients += ",";
        }
        repository.insert(sqlInsertPatients);
    }

    public void removePatient(int patientId){
        String sqlDeletePatient = sqlDelete + patientId + "'";;
        repository.delete(sqlDeletePatient);
    }

    public void printTable(){
        printHeadline("PATIENTS", PATIENTS_ATTRIBUTES);
        printTable(sqlSelectAll);
    }


}
