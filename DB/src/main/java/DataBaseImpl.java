import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseImpl implements DataBase {
    private static final String mySql = "\\DB\\MySQLServer\\bin\\mysqld";
    private static Process mySqlTask; // todo - if error occures sho uld shut down
    private static final Logger logger = Logger.getLogger(DataBaseImpl.class);
    private static Connection conn;
    private static Statement stmt;

    public DataBaseImpl() throws SQLException {
        try{
            mySqlTask = Runtime.getRuntime().exec(System.getProperty("user.dir") + mySql);

            // Step 1: Allocate a database 'Connection' object
            conn = DriverManager.getConnection(
                    //"jdbc:mysql://localhost:3306/doctors_delays?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    "jdbc:mysql://localhost:3306/doctors_delays?useSSL=false",
                    "root", ""); // for MySQL only

            // Step 2: Allocate a 'Statement' object in the Connection
            stmt = conn.createStatement() ;

        } catch (IOException e) {
            String errorMessage = "Couldn't start mysql from path " + System.getProperty("user.dir") + mySql;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
        catch(SQLException e) {
            String errorMessage = "Couldn't create statement ";
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }// Close conn and stmt - Done automatically by try-with-resources

    }

    @Override
    public List<DelayReport> getReports(String doctorsName, Timestamp startTime, Timestamp endTime) {

        try{
            String sqlReportList = "SELECT *" +
                    "               FROM delays " +
                    "               WHERE doctor_name = '" + doctorsName +"' AND " +
                    "                     TIMESTAMP > '" + startTime +"' AND " +
                    "                     TIMESTAMP < '" + endTime +"' " +
                    "               ORDER BY timestamp";

            List<DelayReport> delayReports = new ArrayList<DelayReport>();
            ResultSet reportListSet = stmt.executeQuery(sqlReportList);
            while (reportListSet.next()) {
                String doctorName = reportListSet.getString("doctor_name");
                int delay = reportListSet.getInt("reported_delay_minutes");
                Timestamp ts = reportListSet.getTimestamp("timestamp");
                DelayReport dr = new DelayReport()
                {
                    @Override
                    public String getDoctorsName(){
                        return doctorName;
                    }

                    @Override
                    public int getReportedDelay() {
                        return delay;
                    }

                    @Override
                    public Timestamp getReportTimestamp() {
                        return ts;
                    }
                };
                delayReports.add(dr);
                }
            return delayReports;
            } catch (SQLException ex) {

            String errorMessage = "getReports Exception, Couldn't run the query: \n" +
                    "SELECT * FROM delays WHERE doctor_name = '" + doctorsName +"' AND TIMESTAMP > '" + startTime +"' AND TIMESTAMP < '" + endTime +"' ORDER BY timestamp";
            logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }


    /* this is not the most efficient query for checking whether doctorExists*/
    @Override
    public boolean doctorExists(String doctorsName) {
        try{
            String sqlDoctorExists = "SELECT COUNT(*) AS num_of_reports FROM delays WHERE doctor_name = '" + doctorsName +"'";
            ResultSet countReports = stmt.executeQuery(sqlDoctorExists);
            while (countReports.next()) {
                int number_of_reports = countReports.getInt("num_of_reports");
                if (number_of_reports > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
     catch(SQLException e) {
         String errorMessage = "doctorExists Exception, Couldn't run the query: \n" +
                 "SELECT COUNT(*) AS num_of_reports FROM delays WHERE doctor_name = '" + doctorsName +"'";
         logger.error(errorMessage, e);
         throw new RuntimeException("errorMessage", e);
    }
        return false;
    }

    @Override
    public void addReport(String doctorsName, int expectedDelay) {
        try{
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            /* String ts = timestamp.toString().split("\\.")[0];
             * the above line should delete the numbers after the '.'
             * e.g 2019-11-14 22:21:01 instead of 2019-11-14 22:21:01.0
             * probably this line is not necessary - delete
             */

            String sqlInsert = "INSERT INTO delays" + " VALUES('" + doctorsName+ "','" + expectedDelay + "', '" + timestamp + "')";
            int countInserted = stmt.executeUpdate(sqlInsert);
        }
        catch(SQLException e) {

            String errorMessage = "addReport Exception, Couldn't run the query: \n" +
                    "INSERT INTO delays" + " VALUES('" + doctorsName+ "','" + expectedDelay + "', '" + new Timestamp(System.currentTimeMillis()) + "')";
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }

    }

    private static void printReportsFromList(List<DelayReport> list){
        System.out.println("***********printReportsFromList************");
        int n = list.size();
        for(int i = 0; i < n; i++){
            DelayReport dr = list.get(i);
            System.out.println(dr.getDoctorsName() + ", " + dr.getReportedDelay() + ", " + dr.getReportTimestamp());
        }
    }

    private static void printReports() {
        System.out.println("***********printReports************");
        try {
            String strSelect = "select * from delays";
            ResultSet rset = stmt.executeQuery(strSelect);
            while (rset.next()) {   // Move the cursor to the next row
                System.out.println(rset.getString("doctor_name") + ", "
                        + rset.getInt("reported_delay_minutes") + ", "
                        + rset.getTimestamp("timestamp"));
            }

        } catch (SQLException e) {
            System.out.println("printReports Exception: " + e);
        }
    }


    public static void main(String[] args) {
        try {
            DataBase db = new DataBaseImpl();

            db.addReport( "Bar Gal", 19);

            boolean b = db.doctorExists( "Ientura");
            if (b)
                System.out.println("Dr. Ientura exists");
            else
                System.out.println("Dr. Ientura doesn't exist");


            b = db.doctorExists( "Itay Ventura");
            if (b)
                System.out.println("Dr. Itay Ventura exists\n");
            else
                System.out.println("Dr. Itay Ventura doesn't exist\n");

            List<DelayReport> list = db.getReports("Itay Ventura", Timestamp.valueOf("2019-11-26 10:40:32"), Timestamp.valueOf("2019-11-26 12:40:32"));
            printReportsFromList(list);
            printReports();

        } catch(SQLException e) {
            System.out.println("main Exception: " + e);
        }
    }
}
