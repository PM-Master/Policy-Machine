package gov.nist.policyserver.translator.model.table;

import gov.nist.policyserver.translator.model.row.CompositeRow;

import java.util.ArrayList;
import java.util.List;

/**
 * A table made up of simple tables
 */
public class CompositeTable {
    private List<SimpleTable>  compositeTable;
    private List<CompositeRow> compositeRows;

    public CompositeTable(){
        compositeTable = new ArrayList<>();
        compositeRows = new ArrayList<>();
    }

    public void addSimpleTable(SimpleTable table){
        compositeTable.add(table);
    }

    public List<SimpleTable> getCompositeTable(){
        return compositeTable;
    }

    public SimpleTable getSimpleTable(String tableName){
        for(SimpleTable t : compositeTable){
            if(t.getTableName().equals(tableName)){
                return t;
            }
        }
        return null;
    }

    @Override
    public String toString(){
        String str = "";
        for(SimpleTable t : compositeTable){
            str += t + "\n";
        }
        return str;
    }
}
