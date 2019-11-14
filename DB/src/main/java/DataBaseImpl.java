import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class DataBaseImpl implements DataBase {
    private static String mySqlPath = System.getenv("MY_SQL_PATH");
    private static Process mySql;
    private static final Logger logger = Logger.getLogger(DataBaseImpl.class);


    public DataBaseImpl(){
        try{
            mySql = Runtime.getRuntime().exec(mySqlPath + "bin\\mysqld");
        } catch (IOException e) {
            String errorMessage = "Couldn't start mysql from path " + mySqlPath;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public List<DelayReport> getReports(String doctorsName, LocalDateTime startTime, LocalDateTime endTime) {
        return null; //TODO - implement
    }

    @Override
    public boolean doctorExists(String doctorsName) {
        return false; //TODO - implement
    }

    @Override
    public void addReport(String doctorsName, int expectedDelay) {
        //TODO - implement
    }

    //TODO - this is just an example of using mySQL.
    public static void main(String[] args) {
        DataBase db = new DataBaseImpl();
        try (
                // Step 1: Allocate a database 'Connection' object
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/doctors_delays?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                        "root", ""); // for MySQL only

                // Step 2: Allocate a 'Statement' object in the Connection
                Statement stmt = conn.createStatement();
        ) {
            String sqlInsert = "insert into delays values ('Itay Ventura', 25, '2019-11-12 09:40:50')";
            int countInserted = stmt.executeUpdate(sqlInsert);

            String strSelect = "select * from delays";
            ResultSet rset = stmt.executeQuery(strSelect);
            while(rset.next()) {   // Move the cursor to the next row
                System.out.println(rset.getString("doctor_name") + ", "
                        + rset.getInt("reported_delay_minutes") + ", "
                        + rset.getTimestamp("timestamp"));
            }
        } catch(SQLException e) {
            System.out.println("ooopppsssssss " + e);
        }  // Close conn and stmt - Done automatically by try-with-resources
    }
}
