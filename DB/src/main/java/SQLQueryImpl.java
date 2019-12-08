import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class SQLQueryImpl implements SQLQuery {
    private static Process mySqlTask; // todo - if error occurs should shut down
    private static final String mySql = "\\DB\\MySQLServer\\bin\\mysqld";
    private static Connection conn; //todo conn.close() ?
    static Statement stmt; // todo make it not global, or surround with locks in each execution for concurrency?


    SQLQueryImpl() throws SQLException {
        try {
            mySqlTask = Runtime.getRuntime().exec(System.getProperty("user.dir") + mySql);

            // Step 1: Allocate a database 'Connection' object
            conn = DriverManager.getConnection(
                   "jdbc:mysql://localhost:3306/doctors_delays?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    //"jdbc:mysql://localhost:3308/doctors_delays?useSSL=false",
                    "root", "");

            // Step 2: Allocate a 'Statement' object in the Connection
            stmt = conn.createStatement();


        } catch (IOException e) {
            String errorMessage = "Couldn't start mysql from path " + System.getProperty("user.dir") + mySql;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        } catch (SQLException e) {
            String errorMessage = "Couldn't create statement ";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }// Close conn and stmt - Done automatically by try-with-resources

    }

    public static ResultSet getNumOfTestDoctorReports() {

        String sqlNumOfReports = "Select Count(*) as num_of_reports " +
                "from delays " +
                "where doctor_name LIKE '%" + "testDoctor" + "%'";
        try{
            return stmt.executeQuery(sqlNumOfReports);
        }catch (SQLException e) {
            String errorMessage = "reportsTest SQL Exception, Couldn't run the query: \n" + sqlNumOfReports + "\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    static void deleteTestReports() {
        String sqlDeleteTestReports = "delete from  delays " +
                "where doctor_name LIKE '%" + "testDoctor" + "%'";
        try {
            stmt.executeUpdate(sqlDeleteTestReports);
        } catch (SQLException e) {
            String errorMessage = "addReportTest SQL Exception, Couldn't run the query: \n" + sqlDeleteTestReports + "\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    static void deleteTestDoctors() {
        String sqlDeleteTestDoctors = "delete from  doctors " +
                "where doctor_name LIKE '%" + "testDoctor" + "%'";
        try {
            stmt.executeUpdate(sqlDeleteTestDoctors);
        } catch (SQLException e) {

            String errorMessage = "deleteTestDoctors SQL Exception, Couldn't run the query: \n" + sqlDeleteTestDoctors + "\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public ResultSet getDB(String DB) {
        String sqlSelectAllFromDB = DB.equals("delays")? "select * from " + DB + " ORDER BY timestamp ":"select * from " + DB;

        try {
            return stmt.executeQuery(sqlSelectAllFromDB);
        } catch (SQLException e) {
            String errorMessage = "getColumnsNames Exception, Couldn't run the query: \n" + sqlSelectAllFromDB;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public List<String> getColumnsNames(String DB) {
        try {
            ResultSet resultSet = getDB(DB); /* todo not efficient */
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            List<String> columnsNames = new ArrayList<>();
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames.add(rsmd.getColumnName(i));
            }
            return columnsNames;
        } catch (SQLException e) {
            String errorMessage = "getColumnsNames Exception\n";
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public void insertDoctor(String doctorsName) {
        String sqlInsertDoctor = "INSERT INTO doctors(doctor_name) " +
                "VALUES('" + doctorsName + "')";
        try {
            int countInserted = stmt.executeUpdate(sqlInsertDoctor); //todo count inserted is never used
        } catch (SQLException e) {
            String errorMessage = "insertDoctor Exception, Couldn't run the query: \n" + sqlInsertDoctor;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }

    }

    static void insertReport(String doctorsName, int expectedDelay) {
        String sqlInsertReport = "INSERT INTO delays(doctor_name, reported_delay_minutes)" +
                " VALUES('" + doctorsName + "','" + expectedDelay + "')";
        try {
            int countInserted = stmt.executeUpdate(sqlInsertReport);
        } catch (SQLException e) {
            String errorMessage = "insertReport Exception, Couldn't run the query: \n" + sqlInsertReport;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }

    }

    static void insertReport(String doctorsName, int expectedDelay, Timestamp ts) {
        String tss = "timestamp";
        String sqlInsertReport = "INSERT INTO delays(doctor_name, reported_delay_minutes," + tss + ")" +
                " VALUES('" + doctorsName + "','" + expectedDelay + "','" + ts + "')";
        try {
            int countInserted = stmt.executeUpdate(sqlInsertReport);
        } catch (SQLException e) {
            String errorMessage = "addReport Exception, Couldn't run the query: \n" + sqlInsertReport;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public boolean isDoctorExist(String doctorsName) {
        String sqlIsDoctorExist = "SELECT COUNT(doctor_name) AS isDoctorExist " +
                "FROM doctors " +
                "WHERE doctor_name = '" + doctorsName + "'";
        try {
            ResultSet ResultSetIsDoctorExist = stmt.executeQuery(sqlIsDoctorExist);
            ResultSetIsDoctorExist.next();
            return (ResultSetIsDoctorExist.getInt("isDoctorExist") == 1);
        } catch (SQLException e) {
            String errorMessage = "doctorExists Exception, Couldn't run the query: \n" + sqlIsDoctorExist;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }

    }

    static int getNumberOfDoctors() {
        String sqlDoctorCnt = "select COUNT(doctor_name) as doctorCnt FROM doctors";

        try {
            ResultSet ResultSetDoctorCnt = stmt.executeQuery(sqlDoctorCnt);
            ResultSetDoctorCnt.next();
            return ResultSetDoctorCnt.getInt("doctorCnt");
        } catch (SQLException e) {
            String errorMessage = "getNumberOfDoctors Exception, Couldn't run the query: \n" + sqlDoctorCnt;
            DataBaseImpl.logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

    @Override
    public ResultSet getReports(String doctorsName, Timestamp startTime, Timestamp endTime) {
        String sqlReportsList = "SELECT reported_delay_minutes, timestamp" +
                "               FROM delays " +
                "               WHERE  doctor_name = '" + doctorsName + "' AND " +
                "                     TIMESTAMP > '" + startTime + "' AND " +
                "                     TIMESTAMP < '" + endTime + "' " +
                "               ORDER BY timestamp";
        try {
            return stmt.executeQuery(sqlReportsList);
        } catch (SQLException ex) {

            String errorMessage = "getReports Exception, Couldn't run the query: \n" + sqlReportsList + "\n";
            DataBaseImpl.logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }

    @Override
    public ResultSet getDoctorMetaData(String doctorsName) {
        String sqlDoctorMetaData = "Select  * " +
                "From doctors " +
                "WHERE doctor_name = '" + doctorsName + "'";
        try {
            return stmt.executeQuery(sqlDoctorMetaData);
        } catch (SQLException ex) {
            String errorMessage = "getDoctorMetaData Exception, Couldn't run the query: \n" + sqlDoctorMetaData + "\n";
            DataBaseImpl.logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }

    @Override
    public ResultSet getInterval(String doctorsName) {
        String sqlGetInterval = "Select " + "appointment_interval " +
                "From doctors " +
                "WHERE doctor_name = '" + doctorsName + "'";
        try {
            return stmt.executeQuery(sqlGetInterval);
        } catch (SQLException ex) {
            String errorMessage = "getInterval Exception, Couldn't run the query: \n" + sqlGetInterval + "\n";
            DataBaseImpl.logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }

    @Override
    public ResultSet getDayReports(String doctorsName) {
        String sqlDayReport = "SELECT distinct date(TIMESTAMP) AS DATE " +
                "FROM delays " +
                "              WHERE doctor_name = '" + doctorsName + "'";
        try {
            return stmt.executeQuery(sqlDayReport);
        } catch (SQLException ex) {
            String errorMessage = "getDayReports Exception, Couldn't run the query: \n" + sqlDayReport + "\n";
            DataBaseImpl.logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }

    @Override
    public Time getTime(String time, String doctorsName) {
        String sqlGetTime = "Select " + time + " " +
                "From doctors " +
                "WHERE doctor_name = '" + doctorsName + "'";
        try {
            ResultSet resultSetTime = stmt.executeQuery(sqlGetTime);
            resultSetTime.next();
            return resultSetTime.getTime(time);
        } catch (SQLException ex) {
            String errorMessage = "getStartTime Exception, Couldn't run the query: \n" + sqlGetTime + "\n";
            DataBaseImpl.logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }
    }
}