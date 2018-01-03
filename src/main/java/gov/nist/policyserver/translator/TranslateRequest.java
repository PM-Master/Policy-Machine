package gov.nist.policyserver.translator;

public class TranslateRequest {
    private String sql;
    private String username;
    private String host;
    private int    port;
    private String dbUsername;
    private String dbPassword;
    private String database;

    public TranslateRequest() {

    }

    public TranslateRequest(String sql, String username, String host, int port, String dbUsername, String dbPassword, String database) {
        this.sql = sql;
        this.username = username;
        this.host = host;
        this.port = port;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.database = database;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
