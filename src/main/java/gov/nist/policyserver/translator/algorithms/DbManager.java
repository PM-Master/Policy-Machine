package gov.nist.policyserver.translator.algorithms;

import net.sf.jsqlparser.schema.Column;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DbManager {

    private Connection                    conn;
    private String                        database;
    private List<String>                  rows;
    private HashMap<String, List<String>> tableRows;
    private HashSet<Column>               columns;

    public HashSet<Column> getColumns() {
        return columns;
    }

    public void setColumns(HashSet<Column> columns) {
        this.columns = columns;
    }

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

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public HashMap<String, List<String>> getTableRows() {
        return tableRows;
    }

    public void setTableRows(HashMap<String, List<String>> tableRows) {
        this.tableRows = tableRows;
    }
}
