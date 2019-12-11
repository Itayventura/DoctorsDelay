import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalTime;

/**
 * Handles MySql. Note! should be thread safe.
 */
public interface DataBase {

    public abstract class DelayReport{
        abstract public int getReportedDelay();
        abstract public int getReportTimestamp();
    }

    /**
     *
     * @param doctorsName
     * @param startTime
     * @param endTime
     * @return a list of reports to the specified doctor, in this time range
     */
    List<DelayReport> getReports(String doctorsName, LocalDateTime startTime, LocalDateTime endTime);

    public abstract class DoctorInfo{
        abstract LocalTime getStartTime();
        abstract int getInterval();
    }


    /**
     * Accesses doctors db
     * @param doctorName
     * @return the doctors start time and interval
     */
    DoctorInfo getDoctorInfo(String doctorName);


    public abstract class Appointment{
        abstract LocalDateTime getDateTime();
        abstract String getDoctor();
    }

    /**
     * Accesses this clients db and gets it's **future**
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
     * adds the reported delay to this doctor at current timestamp
     * @param doctorsName
     * @param expectedDelay
     */
    void addReport(String doctorsName, int expectedDelay);//implementation - needs to add current timestamp to db row


    /**
     * @param userId - the user's identity number
     * @return this uses password as written in clients db
     */
    String getUserPassword(int userId);

    /**
     * This is the actual delay a user experienced.
     * This function should somehow fix the data (don't know yet how)
     * @param userId        - the user's identity number
     * @param actualDelay   - the actual delay the user experienced
     */
    void feedbackOnEstimate(int userId, int actualDelay);


}
