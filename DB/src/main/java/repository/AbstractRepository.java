package repository;

import entities.Entity;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** T is {Doctor, Delay, Appointment, Patient} */
public abstract class AbstractRepository<T extends Entity> implements Repository<T> {
    private static final Logger logger = Logger.getLogger(AppointmentsRepository.class);

    SimpleDateFormat sdf = new SimpleDateFormat(entities.Entity.TIMESTAMP_FORMAT);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(entities.Entity.TIMESTAMP_FORMAT);

    /** execute the query and return list of all records from table
     *  this method is for internal use - in order to print table
     *  the difference from select: the return value of this method holds the meta data of the table
     * @param sqlSelectAllFromDB sql query that will be executed
     * @return List of lists whereas the first list is columns' names
     *          and the rest of the lists are records as they appear in the table.
     */
    public static List<List<String>> getTable(String sqlSelectAllFromDB){
        Connection conn = null;
        Statement stmt = null;
        ResultSet DBSet = null;
        List<List<String>> records = new ArrayList<>();

        try {
            conn = DbConnectionImpl.getConnection();
            stmt = conn.createStatement();
            DBSet = stmt.executeQuery(sqlSelectAllFromDB);
            ResultSetMetaData metaDataSet = DBSet.getMetaData();
            int columnsNumber = metaDataSet.getColumnCount();
            List<String> columnsNames = new ArrayList<>();
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames.add(metaDataSet.getColumnName(i));
            }
            records.add(columnsNames);
            while (DBSet.next()) {
                List<String> row = new ArrayList<>();

                for (String columnsName : columnsNames) {
                    String value = DBSet.getString(columnsName);
                    row.add(value);
                }
                records.add(row);
            }
            return records;
        }catch (SQLException e) {
            String errorMessage = "printTable Exception, Couldn't run the query: \n" + sqlSelectAllFromDB;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }finally {
            try {
                if(DBSet != null) DBSet.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } catch (SQLException e) { logger.error("printTable SQLException could not close conn", e);  }
        }

        }

    public void delete(String sqlDelete){
        execute(sqlDelete);
    }

    @Override
    public void update(String sqlUpdate) {
        execute(sqlUpdate);
    }

    @Override
    public void insert(String sqlInsert) {
        execute(sqlInsert);
    }

    private void execute(String sqlQuery){
        try (Connection conn = DbConnectionImpl.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            String errorMessage = "\nSQL Exception, Couldn't run the query: \n" + sqlQuery;
            logger.error(errorMessage, e);
            throw new RuntimeException("errorMessage", e);
        }
    }

}
