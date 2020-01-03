package repository;

import entities.Doctor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorsRepository extends AbstractRepository<Doctor>{
    private static final Logger logger = Logger.getLogger(DoctorsRepository.class);

    @Override
    public List<Doctor> select(String sqlSelect) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet doctorSet = null;
        List<Doctor> doctors = null;
        try {
            conn = DbConnectionImpl.getConnection();
            stmt = conn.createStatement();
            doctorSet = stmt.executeQuery(sqlSelect);
            doctors =  new ArrayList<>();
            while(doctorSet.next()) {
                LocalTime startTime = doctorSet.getTime("startTime").toLocalTime();
                LocalTime endTime = doctorSet.getTime("endTime").toLocalTime();
                int interval = doctorSet.getInt("appointment_interval");
                String type = doctorSet.getString("doctor_type");
                String doctorsName = doctorSet.getString("doctor_name");
                doctors.add(new Doctor(type, doctorsName, startTime, endTime, interval));
            }
        } catch(SQLException ex){
            String errorMessage = "Sql Exception, Couldn't run the query: \n" + sqlSelect + "\n";
            logger.error(errorMessage, ex);
            throw new RuntimeException("errorMessage", ex);
        }finally{
            try {
                if (doctorSet != null) doctorSet.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { logger.error("SQL Exception could not close conn", e);
            }
        }
        return doctors;
    }

}
