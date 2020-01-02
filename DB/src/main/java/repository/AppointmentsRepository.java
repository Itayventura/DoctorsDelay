package repository;

import entities.Appointment;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsRepository extends AbstractRepository<Appointment>{
    private static final Logger logger = Logger.getLogger(AppointmentsRepository.class);

    @Override
    public List<Appointment> select(String sqlSelect) {
        List<Appointment> appointmentList = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet appointmentSet = null;
        try {
            conn = DbConnectionImpl.getConnection();
            stmt = conn.createStatement();
            appointmentSet = stmt.executeQuery(sqlSelect);
            while (appointmentSet.next()) {
                Timestamp appointmentTimestamp = appointmentSet.getTimestamp("appointment_time");
                LocalDateTime dateTime = LocalDateTime.parse(sdf.format(appointmentTimestamp), formatter);
                String doctorsName = appointmentSet.getString("doctor_name");
                appointmentList.add(new Appointment(dateTime.format(formatter)+"", doctorsName));
            }
            return appointmentList;
        } catch (SQLException ex) {
            String errorMessage = "\n SQLException, Couldn't run the query: \n" + sqlSelect + "\n";
            logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        } finally {
            try {
                if (appointmentSet != null) appointmentSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.error("\n SQLException, couldn't close", e);
            }
        }
    }
}
