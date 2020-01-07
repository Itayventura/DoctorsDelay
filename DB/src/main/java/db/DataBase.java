package db;

import entities.Appointment;
import entities.Delay;
import entities.Doctor;
import entities.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DataBase {

    /**
     * adds the delay to this doctor at current timestamp to db
     * @param personalId - the user's identity number
     * @param doctorsName - the doctor's name
     * @param delay - the reported delay in minutes
     * @return a list of reports to the specified doctor, in this date
     */
    void addReport(int personalId, String doctorsName, int delay);


    /**
     * @param doctorsName - the doctor's name
     * @param date - date's format: "yyyy-mm-dd"
     * @return a list of reports to the specified doctor, in this date
     *         or null if doctor does not exist
     */
    List<Delay> getDayReport(String doctorsName, LocalDate date);

    /** get report list of doctor between this time range
     * @param doctorsName the doctor's name
     * @param from start time
     * @param to end time
     * @return a list of reports to the specified doctor, in this time range
     */
    List<Delay> getReports(String doctorsName, LocalDateTime from, LocalDateTime to);

    /** select from doctors where doctor_name = doctorsName and return new Doctor same as table
     *
     * @param doctorsName
     * @return Doctor with attributes like in table
     */
    Doctor getDoctor(String doctorsName);


    /**
     * Accesses this patients' db and gets it's **future**
     * appointments (checks if their LocalDateTime is after current)
     * @param userId - the user's identity number
     * @return a list if the user's future appointments
     */
    List<Appointment> getUserFutureAppointments(int userId);

    /**
     * @param doctorsName
     * @return true if doctor exists in HMO doctors db
     */
    boolean doctorExists(String doctorsName);

    /**
     * @param userId - the user's identity number
     * @return this uses password as written in patients' db. null if  user is not in the system
     */
    String getUserPassword(int userId);

    /** adds report with report_type = 'feedback' to data base
     *  @param userId        - the user's identity number
     * @param actualDelay   - the actual delay the user experienced
     */
    void feedbackOnEstimate(int userId, int actualDelay);
    //todo different from interface

    /** adds score added to patient with id userId
     *
     * @param userId id of the patient
     * @param scoreAdded score added to patient's score
     */
    public void addScore(int userId, int scoreAdded);

    /**
     * @param userId id of patient
     * @return patient's score
     */
    public int getScore(int userId);

    /**
     *
     * @param userId user's id
     * @return
     */
    public Appointment getLastAppointment(int userId);

    public List<Doctor> getDoctors();

    public List<Delay> getDelays(String doctorsName);


    /** prints all records from tableName in database */
    void printTable(String tableName);

}
