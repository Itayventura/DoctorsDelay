import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles MySql. Note! should be thread safe.
 */
public interface DataBase {

    abstract class DelayReport{
        private int reportedDelay; //the expected delay that the client reported
        private Timestamp reportTimestamp;
        private String doctorsName;

        //public void setReportedDelay(int delay){reportedDelay = delay;}
        //public void setTimestamp(Timestamp timestamp){reportTimestamp = timestamp;}

        abstract public int getReportedDelay();
        abstract public Timestamp getReportTimestamp();
        abstract public String getDoctorsName();

    }

    List<DelayReport> getReports(String doctorsName, Timestamp startTime, Timestamp endTime);

    boolean doctorExists(String doctorsName);

    void addReport(String doctorsName, int expectedDelay);//implementation - needs to add current timestamp to db row

}
