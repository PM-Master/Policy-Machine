package gov.nist.policyserver.translator;

public class TranslateResponse {
    String id;
    String sql;

    public TranslateResponse() {

    }

    public TranslateResponse(String id, String sql) {
        this.id = id;
        this.sql = sql;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
