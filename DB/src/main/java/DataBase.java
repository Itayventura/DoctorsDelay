import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles MySql. Note! should be thread safe.
 */
public interface DataBase {

    abstract class DelayReport{
        //private int reportedDelay;
        //private Timestamp reportTimestamp;

        abstract public int getReportedDelay();
        abstract public Timestamp getReportTimestamp();
    }

    abstract class DayReport{
        //private List<DelayReport> delayReportList;

        abstract public List<DelayReport> getReportList();
        abstract public void printDayReport();
        abstract public void printReportsBetween(Time startTime, Time endTime);
        abstract public DayOfWeek getDay();
        abstract public List<DelayReport> getReportsBetween(Time startTime, Time endTime);
    }

    abstract class DoctorReport{
        //private String doctorName;
        //private int interval;

        abstract public List<DayReport> getDayReports();
        abstract public String getDoctorName();
        abstract public int getInterval();
        // add minimum treatment time 2 minutes
        abstract public void printDoctorReport();

    }

    List<DelayReport> getReports(String doctorsName, Timestamp startTime, Timestamp endTime);

    boolean doctorExists(String doctorsName);

    void addReport(String doctorsName, int expectedDelay);

    DayReport getDayReport(String doctorsName, String date);

    DoctorReport getDoctorReport(String doctorsName);
}
