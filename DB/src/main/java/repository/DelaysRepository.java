package repository;

import entities.Delay;
import entities.Entity;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DelaysRepository extends AbstractRepository<Delay> {
    private static final Logger logger = Logger.getLogger(DelaysRepository.class);

    @Override
    public List<Delay> select(String sqlSelect) {

        Connection conn = null;
        Statement stmt = null;
        ResultSet delaysReportSet = null;
        try {
            conn = DbConnectionImpl.getConnection();
            stmt = conn.createStatement();
            delaysReportSet = stmt.executeQuery(sqlSelect);
            List<Delay> delays = new ArrayList<>();
            while (delaysReportSet.next()) {
                int reportedDelayMinutes = delaysReportSet.getInt("reported_delay_minutes");
                Entity.Type reportType = Entity.Type.valueOf(delaysReportSet.getString("report_type").toUpperCase());
                Timestamp timestamp = delaysReportSet.getTimestamp("timestamp");
                LocalDateTime dateTime = LocalDateTime.parse(sdf.format(timestamp), formatter);
                delays.add(new Delay(reportedDelayMinutes, dateTime.format(formatter)+"", reportType));
            }
            return delays;
        } catch (SQLException ex) {
            String errorMessage = "select SQLException, Couldn't run the query: \n" + sqlSelect + "\n";
            logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        } finally {
            try {
                if (delaysReportSet != null) delaysReportSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("select Sql Exception couldn't close connection", e);
            }
        }
    }
}

