package gov.nist.policyserver.requests;

public class DataRequest {
    String host;
    int    port;
    String username;
    String password;
    String schema;
    String table;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }


    public void setSchema(String schema) {
        this.schema = schema;
    }
}
