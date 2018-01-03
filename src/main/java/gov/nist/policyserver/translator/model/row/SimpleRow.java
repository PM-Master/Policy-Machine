package gov.nist.policyserver.translator.model.row;

public class SimpleRow {
    private String rowName;
    private String tableName;

    public SimpleRow(String tableName, String rowName) {
        this.rowName = rowName;
        this.tableName = tableName;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String toString() {
        return this.tableName + "." + this.rowName;
    }
}
