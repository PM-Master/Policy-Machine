package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.SelectUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateAlgorithm extends Algorithm {
    private Update update;

    public UpdateAlgorithm(Update update, PmManager pm, DbManager db){
        super(pm, db);
        this.update = update;
    }

    @Override
    public String run() throws SQLException, IOException, PolicyMachineException, PMAccessDeniedException, JSQLParserException, NodeNotFoundException, InvalidNodeTypeException, NoUserParameterException, NameInNamespaceNotFoundException, InvalidPropertyException {
        //determine the rows that are going to be updated
        List<String> rows = getTargetRows();

        //check each row that the user has write access to each row,column
        checkRows(rows);

        //return update statement
        return update.toString();
    }

    public List<String> getTargetRows() throws SQLException {
        String tableName = update.getTable().getName();
        List<String> keys = getKeys(tableName);

        //build select
        Select select = buildSelect(tableName, keys, update.getWhere());

        //return rows
        return getRows(select);
    }

    private List<String> getRows(Select select) throws SQLException {
        List<String> rows = new ArrayList<String>();

        ResultSet rs = dbManager.getConnection().createStatement().executeQuery(select.toString());
        ResultSetMetaData meta = rs.getMetaData();
        int numCols = meta.getColumnCount();
        while (rs.next()) {
            String rowName = "";
            for (int i = 1; i <= numCols; i++) {
                String value = rs.getString(i);
                if(i == 1){
                    rowName += value;
                }else{
                    rowName += PmManager.NAME_DELIM + value;
                }
            }
            rows.add(rowName);
        }

        return rows;
    }

    private Select buildSelect(String tableName, List<String> keys, Expression where){
        Select select = SelectUtils.buildSelectFromTable(new Table(tableName));
        PlainSelect ps = (PlainSelect) select.getSelectBody();

        List<SelectItem> selectItems = new ArrayList<>();
        for(String key : keys){
            selectItems.add(new SelectExpressionItem(new Column(key)));
        }

        ps.setSelectItems(selectItems);
        ps.setWhere(where);

        return select;
    }

    private void checkRows(List<String> rows) throws IOException, PolicyMachineException, PMAccessDeniedException, JSQLParserException, InvalidNodeTypeException, NodeNotFoundException, NoUserParameterException, NameInNamespaceNotFoundException, InvalidPropertyException {
        List<String> reqColumns = update.getColumns().stream().map(
                Column::getColumnName).collect(Collectors.toList());

        for (String rowName : rows) {
            long rowPmId = pmManager.getEntityId(update.getTable().getName(), rowName);

            //iterate through the requested columns and find the intersection of the current row and current column
            for (String columnName : reqColumns) {
                long columnPmId = pmManager.getEntityId(update.getTable().getName(), columnName);
                //if the intersection (an object) is in the accessible children add the COLUMN to a list
                //else if not in accChildren, check if its in where clause

                if(!checkColumn(columnPmId, rowPmId, PmManager.FILE_WRITE)){
                    throw new PMAccessDeniedException(columnName);
                }
            }


            //check there are no inaccessible columns in where clause for this row
            //get all columns in where clause
            //check each column with current row

            HashSet<Column> whereColumns = getWhereColumns(update.getWhere());
            for(Column column : whereColumns){
                if(!reqColumns.contains(column.getColumnName())){
                    long columnPmId = pmManager.getEntityId(update.getTable().getName(), column.getColumnName());

                    //if the intersection (an object) is in the accessible children add the COLUMN to a list
                    //else if not in accChildren, check if its in where clause

                    if(!checkColumn(columnPmId, rowPmId, PmManager.FILE_READ)){
                        throw new PMAccessDeniedException(column.toString());
                    }
                }
            }
        }
    }
}
