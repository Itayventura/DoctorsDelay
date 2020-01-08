package repository;

import entities.Entity;
import entities.Patient;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientsRepository extends AbstractRepository<Patient>{
    private static final Logger logger = Logger.getLogger(PatientsRepository.class);

    @Override
    public List<Patient> select(String sqlSelect) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet patientSet = null;
        List<Patient> patient = new ArrayList<>();
        try {
            conn = DbConnectionImpl.getConnection();
            stmt = conn.createStatement();
            patientSet = stmt.executeQuery(sqlSelect);
            while(patientSet.next()){
                String id = patientSet.getString("patient_id");
                String password = patientSet.getString("password");
                int score = patientSet.getInt("score");
                Entity.Type reportType = Entity.Type.valueOf(patientSet.getString("type").toUpperCase());
                patient.add(new Patient(id, password, score, reportType));
            }

        } catch (SQLException e) {
            String errorMessage = "select sql Exception, Couldn't run the query: \n" + sqlSelect;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        } finally {
            try {
                if(patientSet != null) patientSet.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } catch (SQLException e) { logger.error("select SQL Exception, couldn't close connection", e);  }
        }
        return patient;
    }

}
