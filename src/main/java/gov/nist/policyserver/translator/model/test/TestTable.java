package gov.nist.policyserver.translator.model.test;

import java.util.List;

public class TestTable {
    private String       tableName;
    private List<String> keys;
    private List<String> rowNames;

    public TestTable(String tableName, List<String> keys, List<String> rowNames) {
        this.tableName = tableName;
        this.keys = keys;
        this.rowNames = rowNames;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getRowNames() {
        return rowNames;
    }

    public void setRowNames(List<String> rowNames) {
        this.rowNames = rowNames;
    }
}
