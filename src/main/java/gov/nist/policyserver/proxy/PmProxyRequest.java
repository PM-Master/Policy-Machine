package gov.nist.policyserver.proxy;

public class PmProxyRequest {
    private String sql;
    private String host;
    private int    port;
    private String dbUsername;
    private String dbPassword;
    private String database;

    public PmProxyRequest() {

    }

    public PmProxyRequest(String sql, String host, int port, String dbUsername, String dbPassword, String database) {
        this.sql = sql;
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
