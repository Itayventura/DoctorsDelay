import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

interface SQLQuery {
    //todo this interface is temporary, for internal use

    ResultSet getDB(String DB);

    List<String> getColumnsNames(String DB);

    void insertDoctor(String doctorsName);

    boolean isDoctorExist(String doctorsName);

    ResultSet getReports(String doctorsName, Timestamp startTime, Timestamp endTime);

    ResultSet getDoctorMetaData(String doctorsName);

    ResultSet getInterval(String doctorsName);

    ResultSet getDayReports(String doctorsName);

    Time getTime(String time, String doctorsName);
}
