package repository;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** this class holds a connection pool
 *  can handle 100 statements at a time!
 *  this class implemented as a singleton design pattern
 */
class DbConnectionImpl{

    private static BasicDataSource dataSource;
    private static DbConnectionImpl instance;

    private DbConnectionImpl(){
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://localhost:3306/doctors_delays?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
        ds.setUsername("root");
        ds.setPassword("");
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        dataSource = ds;
    }

    static Connection getConnection() throws SQLException
    {
        if (instance == null)
            instance = new DbConnectionImpl();
        return dataSource.getConnection();
    }



}
