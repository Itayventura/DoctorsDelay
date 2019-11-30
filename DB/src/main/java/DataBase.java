import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles MySql. Note! should be thread safe.
 */
public interface DataBase {

    abstract class DelayReport{
        private int reportedDelay; //the expected delay that the client reported
        private LocalDateTime reportTimestamp; //the time of the report (when client reported)

        abstract public int getReportedDelay();
        abstract public LocalDateTime getReportTimestamp();
    }

    List<DelayReport> getReports(String doctorsName, LocalDateTime startTime, LocalDateTime endTime);

    boolean doctorExists(String doctorsName);

    void addReport(String doctorsName, int expectedDelay);//implementation - needs to add current timestamp to db row

}
