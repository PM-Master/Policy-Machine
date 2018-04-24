package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.evr.exceptions.InvalidEntityException;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public abstract class Algorithm {
    protected String    id;
    protected PmManager pmManager;
    protected DbManager dbManager;

    public Algorithm(String id, PmManager pm, DbManager db) {
        this.id = id;
        this.pmManager = pm;
        this.dbManager = db;
    }

    public String getId() {
        return this.id;
    }

    public abstract String run() throws SQLException, IOException, PolicyMachineException, PmException, JSQLParserException, InvalidEntityException;

    protected List<String> getKeys(String tableName) throws SQLException {
        PreparedStatement ps2 = dbManager.getConnection().prepareStatement("SELECT k.COLUMN_NAME\n" +
                "FROM information_schema.table_constraints t\n" +
                "LEFT JOIN information_schema.key_column_usage k\n" +
                "USING(constraint_name,table_schema,table_name)\n" +
                "WHERE t.constraint_type='PRIMARY KEY'\n" +
                "    AND t.table_schema=DATABASE()\n" +
                "    AND t.table_name='" + tableName + "' order by ordinal_position;");
        ResultSet rs2 = ps2.executeQuery();
        List<String> keys = new ArrayList<>();
        if (rs2 != null) {
            while (rs2.next()) {
                keys.add(tableName + "." + rs2.getString(1));
            }
        }
        return keys;
    }

    protected HashSet<Column> getWhereColumns(Expression where) throws JSQLParserException {
        final HashSet<Column> visitedColumns = new HashSet<>();

        if(where == null){
            return visitedColumns;
        }

        Expression ex = CCJSqlParserUtil.parseCondExpression(where.toString());
        ex.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                visitedColumns.add(column);
            }

            @Override
            public void visit(SubSelect subSelect) {
                PlainSelect select = (PlainSelect) subSelect.getSelectBody();

                for (SelectItem s : select.getSelectItems()) {
                    s.accept(new SelectItemVisitor() {
                        @Override
                        public void visit(AllColumns allColumns) {

                        }

                        @Override
                        public void visit(AllTableColumns allTableColumns) {

                        }

                        @Override
                        public void visit(SelectExpressionItem selectExpressionItem) {
                            selectExpressionItem.getExpression().accept(new ExpressionVisitorAdapter(){
                                public void visit(Column c){
                                    visitedColumns.add(c);
                                }
                            });
                        }
                    });
                }

                try {
                    Expression ex = CCJSqlParserUtil.parseCondExpression(select.getWhere().toString());
                    ex.accept(this);
                } catch (JSQLParserException e) {
                    e.printStackTrace();
                }

            }
        });

        return visitedColumns;
    }

    public boolean checkColumn(long columnPmId, long rowPmId, String perm) throws IOException, PolicyMachineException, NodeNotFoundException, NoUserParameterException, InvalidNodeTypeException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException {
        List<Node> accChildren = pmManager.getAccessibleChildren(rowPmId, perm);

        Node intersection = pmManager.getIntersection(columnPmId, rowPmId);

        return accChildren.contains(intersection);
    }
}
