package gov.nist.policyserver.translator.algorithms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {

    private Connection conn;
    private String database;

    public void setDatabase(String db) throws SQLException {
        this.database = db;
        java.sql.Statement stmt = conn.createStatement();
        stmt.executeUpdate("use " + database);
    }

    public void setConnection(String host, int port, String userId, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, userId, password);
    }

    public Connection getConnection(){
        return conn;
    }

    public String getDatabase(){
        return database;
    }
}
