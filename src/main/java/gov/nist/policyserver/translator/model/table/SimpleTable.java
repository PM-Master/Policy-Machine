package gov.nist.policyserver.translator.model.table;

import gov.nist.policyserver.translator.model.row.SimpleRow;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple table that contains all of the information(columns, keys, rows)
 */
public class SimpleTable {
    private String          tableName;
    private List<Column>    columns;
    private List<String>    keys;
    private List<SimpleRow> rows;

    public SimpleTable(String tableName){
        this.tableName = tableName;
        columns = new ArrayList<>();
        keys = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        if(columns == null) {
            columns = new ArrayList<>();
        }
        this.columns = columns;
    }

    public void addColumn(Column column){
        this.columns.add(column);
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<SimpleRow> getRows() {
        return rows;
    }

    public void setRows(List<SimpleRow> rows) {
        this.rows = rows;
    }

    public void addRow(SimpleRow row){
        this.rows.add(row);
    }

    @Override
    public String toString(){
        String rows = "";
        for(SimpleRow row : this.rows) {
            rows += row + "\n";
        }
        String str =
                "====================\ntableName:\t" + tableName +
                "\nkeys:\t\t" + keys +
                "\ncolumns:\t" + columns +
                "\nrows\n\t{" +
                "\n\t\t\t" + rows +
                "\n\t}\n====================";
        return str;
    }
}
