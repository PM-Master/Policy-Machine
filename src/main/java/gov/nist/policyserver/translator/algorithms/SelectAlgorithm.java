package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import gov.nist.policyserver.translator.model.row.CompositeRow;
import gov.nist.policyserver.translator.model.row.SimpleRow;
import gov.nist.policyserver.translator.model.table.CompositeTable;
import gov.nist.policyserver.translator.model.table.SimpleTable;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SelectAlgorithm extends Algorithm {
    private static final String ROW_NOT_AVAILABLE = "ROW_NOT_AVAILABLE";
    private Select select;
    private List<SelectItem> initialColumns = new ArrayList<>();
    private CompositeTable     compositeTable;
    private List<CompositeRow> compositeRows;

    public SelectAlgorithm(Select select, PmManager pm, DbManager db){
        super(pm, db);
        this.select = select;
        compositeTable = new CompositeTable();
        compositeRows = new ArrayList<>();
    }

    private void log(Object o){
        System.out.println(o);
    }

    @Override
    public String run() throws SQLException, JSQLParserException, PMAccessDeniedException, PolicyMachineException, IOException, InvalidNodeTypeException, InvalidPropertyException, NameInNamespaceNotFoundException, NodeNotFoundException, NoUserParameterException {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        log("SELECT: " + plainSelect);

        initialColumns = plainSelect.getSelectItems();
        log("initial columns: " + initialColumns);

        long s = System.nanoTime();
        setTargetRows(plainSelect);
        long e = System.nanoTime();
        System.out.println("1: " + (e-s));

        s = System.nanoTime();
        Hashtable<List<Column>, List<CompositeRow>> groups = groupRows();
        e = System.nanoTime();
        System.out.println("2: " + (e-s));

        log("groups{");
        for(List<Column> cols : groups.keySet()){
            log(cols + " ---> ");
            List<CompositeRow> row = groups.get(cols);
            row.forEach(this::log);
        }
        log("}");


        //build permitted SQL statements
        s = System.nanoTime();
        String permittedSelect = getPermittedSelect(plainSelect, groups);
        e = System.nanoTime();
        System.out.println("3: " + (e-s));

        return permittedSelect;
    }

    private HashMap<String, List<Column>> originalColumns = new HashMap<>();
    private void setTargetRows(PlainSelect select) throws SQLException {
        //get all tables referenced in the select
        List<String> tables = new ArrayList<>();
        tables.add(select.getFromItem().toString());
        if(select.getJoins() != null && !select.getJoins().isEmpty()){
            tables.addAll(select.getJoins().stream().map(j -> j.getRightItem().toString()).collect(Collectors.toList()));
        }

        List<SelectItem> selectItems = select.getSelectItems();
        for(SelectItem item : selectItems) {
            item.accept(new SelectItemVisitor() {
                @Override
                public void visit(AllColumns allColumns) {}

                @Override
                public void visit(AllTableColumns allTableColumns) {}

                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    selectExpressionItem.getExpression().accept(new PmExpressionVisitor());
                }
            });
        }

        //get the keys of each table referenced in the requested columns
        //these will be the requested columns for the new select
        List<String> tableKeys = new ArrayList<>();
        for(String table : tables){
            SimpleTable simpleTable = new SimpleTable(table);
            simpleTable.setColumns(originalColumns.get(table));

            compositeTable.addSimpleTable(simpleTable);

            List<String> keys = getKeys(table);
            tableKeys.addAll(keys);

            compositeTable.getSimpleTable(table).setKeys(keys);
        }

        //create the new select items to replace the initial ones
        List<SelectItem> newSelectItems = new ArrayList<>();
        for(String key : tableKeys) {
            newSelectItems.add(new SelectExpressionItem(new Column(key)));
        }
        select.setSelectItems(newSelectItems);

        String newSql = select.toString();

        java.sql.Statement stmt =  dbManager.getConnection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        ResultSet rs = stmt.executeQuery(newSql);
        ResultSetMetaData meta = rs.getMetaData();
        int numCols = meta.getColumnCount();

        //add the columns to the SimpleTables
        for (int i = 1; i <= numCols; i++) {
            String table = meta.getTableName(i);
            String column = meta.getColumnName(i);

            SimpleTable t = compositeTable.getSimpleTable(table);
            if(!t.getColumns().contains(new Column(new Table(table), column))) {
                t.addColumn(new Column(new Table(table), column));
            }
        }

        //determine which rows belong to which tables
        while(rs.next()){
            SimpleRow curRow = null;
            String curTable = meta.getTableName(1);
            CompositeRow cr = new CompositeRow();
            String curRowName = "";
            for(int i = 1; i <= numCols; i++){
                String table = meta.getTableName(i);
                if(!table.equals(curTable)) {
                    // start a new table
                    System.out.println("1: " + curRowName);
                    curRow = new SimpleRow(curTable, curRowName);
                    cr.addToRow(curRow);
                    compositeTable.getSimpleTable(curTable).addRow(curRow);

                    curRowName = "";
                    curTable = table;
                }

                // add to current row
                String value = rs.getString(i);
                if(curRowName.isEmpty()){
                    curRowName += value == null ? ROW_NOT_AVAILABLE : value;
                }else{
                    if(!curRowName.equals(ROW_NOT_AVAILABLE)) {
                        curRowName += PmManager.NAME_DELIM + value;
                    }
                }
            }
            System.out.println("2: " + curRowName);
            curRow = new SimpleRow(curTable, curRowName);
            cr.addToRow(curRow);
            compositeTable.getSimpleTable(curTable).addRow(curRow);

            System.out.println("CompositeRow: " + cr);
            compositeRows.add(cr);
        }
        System.out.println(compositeTable);
    }

    private Hashtable groupRows() throws IOException, PolicyMachineException, PMAccessDeniedException, JSQLParserException, InvalidPropertyException, InvalidNodeTypeException, NameInNamespaceNotFoundException, NodeNotFoundException, NoUserParameterException {
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        Hashtable<CompositeRow, List<Column>> results = new Hashtable<>();
        Hashtable<List<Column>, List<CompositeRow>> subsets = new Hashtable<>();

        for (CompositeRow compositeRow : compositeRows) {
            List<Column> okColumns = new ArrayList<>();
            HashSet<Column> whereColumns = new HashSet<>();

            for(SimpleRow row : compositeRow.getCompositeRow()) {
                if(row.getRowName().equals(ROW_NOT_AVAILABLE)) {
                    continue;
                }
                long rowPmId = pmManager.getEntityId(row.getTableName(), row.getRowName());//pmManager.getEntityId(row.getPath(), "b").get(0);

                List<Column> columns = compositeTable.getSimpleTable(row.getTableName()).getColumns();

                //iterate through the requested columns and find the intersection of the current row and current column
                for (Column column : columns) {
                    //if column is not in the initial columns than skip it
                    boolean skip = true;
                    for (SelectItem item : initialColumns) {
                        if(column.toString().endsWith(item.toString())){
                            skip = false;
                            break;
                        }
                    }
                    if (skip) continue;

                    long columnPmId = pmManager.getEntityId(row.getTableName(), column.getColumnName());

                    //if the intersection (an object) is in the accessible children add the COLUMN to a list
                    //else if not in accChildren, check if its in where clause

                    if (checkColumn(columnPmId, rowPmId, PmManager.FILE_READ)) {
                        okColumns.add(column);
                    }
                    addToSet(getWhereColumns(plainSelect.getWhere()), whereColumns);
                }

                //iterate through all where columns
                //  if whereColumn is in requested columns its already been visited
                //  else check if its accessible
                for (Column column : whereColumns) {
                    System.out.println("Checking column " + column + " in where clause");
                    if(columnInList(compositeTable.getSimpleTable(row.getTableName()).getColumns(), column)){
                        long columnPmId = pmManager.getEntityId(row.getTableName(), column.getColumnName());

                        if (!checkColumn(columnPmId, rowPmId, PmManager.FILE_READ)) {
                            throw new PMAccessDeniedException(column.toString());
                        }
                    }
                }
            }

            if (!okColumns.isEmpty()) {
                List<Column> existingColumns = results.get(compositeRow) != null ? results.get(compositeRow) : new ArrayList<>();
                existingColumns.addAll(okColumns);
                results.put(compositeRow, existingColumns);
            }
        }

        //Build the groups of rows based on accessible columns
        //a row is actually a list of object attributes
        for (CompositeRow row : results.keySet()) {
            //row = a list of indiviual OAs that make one row
            List<Column> columns = results.get(row);
            List<CompositeRow> subRows = subsets.get(columns);
            if (subRows != null && !subRows.contains(row)) {
                subRows.add(row);
            }else{
                subRows = new ArrayList<>();
                subRows.add(row);
            }
            subsets.put(columns, subRows);
        }
        return subsets;
    }

    /**
     * create the permitted select to send to the database
     * @param select the initial select
     * @param groups the groups of (column combination) -> (rows)
     * @return
     */
    List<Column> selectColumns = new ArrayList<>();
    private String getPermittedSelect(PlainSelect select, Hashtable<List<Column>, List<CompositeRow>> groups){
        List<Select> selects = new ArrayList<>();
        for(List<Column> columns : groups.keySet()){
            List<CompositeRow> rows = groups.get(columns);

            //the list of columns for this subset including null
            for(SelectItem initColumn : initialColumns){
                initColumn.accept(new SelectItemVisitor() {
                    @Override
                    public void visit(AllColumns allColumns) {}
                    @Override
                    public void visit(AllTableColumns allTableColumns) {}
                    @Override
                    public void visit(SelectExpressionItem selectExpressionItem) {
                        selectExpressionItem.getExpression().accept(new ExpressionVisitorAdapter() {
                            @Override
                            public void visit(Column c) {
                                if (columnInList(columns, c)){
                                    selectColumns.add(new Column(initColumn.toString()));
                                    return;
                                }
                                selectColumns.add(new Column("null AS '" + c.toString() + "'"));
                            }
                        });
                    }
                });
            }

            Expression where = null;
            boolean first = true;
            for(SimpleTable table : compositeTable.getCompositeTable()){
                List<String> keys = table.getKeys();
                String concatKey = "concat(";
                for(int i = 0; i < keys.size(); i++){
                    if(i == 0) {
                        concatKey += keys.get(i);//PmManager.getColumnPath(dbManager.getDatabase(), table, keys.get(i));
                    }else{
                        concatKey += ",'" + PmManager.NAME_DELIM + "'," + keys.get(i);
                    }
                }
                concatKey += ")";

                boolean bOR = false;
                ExpressionList inRows = new ExpressionList();
                List<Expression> expList = new ArrayList<>();//rows.stream().map(StringValue::new).collect(Collectors.toList());
                for(CompositeRow compositeRow : rows){
                    for(SimpleRow row : compositeRow.getCompositeRow()){
                        if(row.getTableName().equals(table.getTableName())) {
                            if(row.getRowName().equals(ROW_NOT_AVAILABLE)) {
                                bOR = true;
                            } else {
                                expList.add(new Column("'" + row.getRowName() + "'"));
                            }
                        }
                    }
                }

                inRows.setExpressions(expList);

                if(!inRows.getExpressions().isEmpty()) {
                    Expression e = new InExpression(new Column(concatKey), inRows);
                    if (first) {
                        where = e;
                    }
                    else {
                        if (bOR) {
                            where = new OrExpression(where, e);
                        }
                        else {
                            where = new AndExpression(where, e);
                        }
                    }
                    first = false;
                }
            }

            Select newSelect = SelectUtils.buildSelectFromTableAndSelectItems(new Table(select.getFromItem().toString()), initialColumns.toArray(new SelectItem[initialColumns.size()]));
            PlainSelect ps = (PlainSelect) newSelect.getSelectBody();

            ps.setJoins(select.getJoins());

            List<SelectItem> selectItems = selectColumns.stream().map(SelectExpressionItem::new).collect(Collectors.toList());

            ps.setSelectItems(selectItems);
            ps.setWhere(where);

            selects.add(newSelect);

            selectColumns.clear();
        }

        String permittedSQL = "";
        for(int i = 0; i < selects.size(); i++){
            String s = "(" + selects.get(i) + ")";
            if(i == 0){
                permittedSQL += s;
            }else{
                permittedSQL += " UNION " + s;
            }
        }

        //first: order
        if(select.getOrderByElements() != null && select.getOrderByElements().size() > 0){
            String order = " ORDER BY ";
            for(OrderByElement o : select.getOrderByElements()){
                order += o + ", ";
            }
            order = order.substring(0, order.length()-2);
            permittedSQL += order;
        }

        //next: limit
        if(select.getLimit() != null){
            permittedSQL += select.getLimit();
        }

        //TODO add order by group by, limit
        return permittedSQL;
    }

    private void addToSet(HashSet<Column> columns, HashSet<Column> target) {
        if(target.isEmpty()){
            target.addAll(columns);
            return;
        }

        for (Column column : columns) {
            Iterator iter2 = target.iterator();
            boolean found = false;
            while (iter2.hasNext()) {
                Column column2 = (Column) iter2.next();
                if (column.getColumnName().equals(column2.getColumnName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                target.add(column);
            }
        }
    }

    private boolean columnInList(List<Column> columns, Column column) {
        for(Column c : columns){
            if(c.getColumnName().equals(column.getColumnName())){
                return true;
            }
        }
        return false;
    }

    class PmExpressionVisitor implements ExpressionVisitor {
        @Override
        public void visit(NullValue nullValue) {

        }

        @Override
        public void visit(Function function) {

        }

        @Override
        public void visit(SignedExpression signedExpression) {

        }

        @Override
        public void visit(JdbcParameter jdbcParameter) {

        }

        @Override
        public void visit(JdbcNamedParameter jdbcNamedParameter) {

        }

        @Override
        public void visit(DoubleValue doubleValue) {

        }

        @Override
        public void visit(LongValue longValue) {

        }

        @Override
        public void visit(DateValue dateValue) {

        }

        @Override
        public void visit(TimeValue timeValue) {

        }

        @Override
        public void visit(TimestampValue timestampValue) {

        }

        @Override
        public void visit(Parenthesis parenthesis) {

        }

        @Override
        public void visit(StringValue stringValue) {

        }

        @Override
        public void visit(Addition addition) {

        }

        @Override
        public void visit(Division division) {

        }

        @Override
        public void visit(Multiplication multiplication) {

        }

        @Override
        public void visit(Subtraction subtraction) {

        }

        @Override
        public void visit(AndExpression andExpression) {

        }

        @Override
        public void visit(OrExpression orExpression) {

        }

        @Override
        public void visit(Between between) {

        }

        @Override
        public void visit(EqualsTo equalsTo) {

        }

        @Override
        public void visit(GreaterThan greaterThan) {

        }

        @Override
        public void visit(GreaterThanEquals greaterThanEquals) {

        }

        @Override
        public void visit(InExpression inExpression) {

        }

        @Override
        public void visit(IsNullExpression isNullExpression) {

        }

        @Override
        public void visit(LikeExpression likeExpression) {

        }

        @Override
        public void visit(MinorThan minorThan) {

        }

        @Override
        public void visit(MinorThanEquals minorThanEquals) {

        }

        @Override
        public void visit(NotEqualsTo notEqualsTo) {

        }

        @Override
        public void visit(Column column) {
            List<Column> cols = originalColumns.get(column.getTable().getName());
            if(cols == null) {
                cols = new ArrayList<>();
            }
            cols.add(column);
            originalColumns.put(column.getTable().getName(), cols);
        }

        @Override
        public void visit(SubSelect subSelect) {

        }

        @Override
        public void visit(CaseExpression caseExpression) {

        }

        @Override
        public void visit(WhenClause whenClause) {

        }

        @Override
        public void visit(ExistsExpression existsExpression) {

        }

        @Override
        public void visit(AllComparisonExpression allComparisonExpression) {

        }

        @Override
        public void visit(AnyComparisonExpression anyComparisonExpression) {

        }

        @Override
        public void visit(Concat concat) {

        }

        @Override
        public void visit(Matches matches) {

        }

        @Override
        public void visit(BitwiseAnd bitwiseAnd) {

        }

        @Override
        public void visit(BitwiseOr bitwiseOr) {

        }

        @Override
        public void visit(BitwiseXor bitwiseXor) {

        }

        @Override
        public void visit(CastExpression castExpression) {

        }

        @Override
        public void visit(Modulo modulo) {

        }

        @Override
        public void visit(AnalyticExpression analyticExpression) {

        }

        @Override
        public void visit(ExtractExpression extractExpression) {

        }

        @Override
        public void visit(IntervalExpression intervalExpression) {

        }

        @Override
        public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {

        }

        @Override
        public void visit(RegExpMatchOperator regExpMatchOperator) {

        }
    }
}

