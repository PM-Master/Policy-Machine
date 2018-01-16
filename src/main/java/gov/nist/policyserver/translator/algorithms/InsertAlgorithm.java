package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InsertAlgorithm extends Algorithm{
    private Insert insert;

    public InsertAlgorithm(Insert insert, PmManager pm, DbManager db) {
        super(pm, db);
        this.insert = insert;
    }

    @Override
    public String run() throws SQLException, IOException, PolicyMachineException, PMAccessDeniedException, JSQLParserException, InvalidPropertyException, InvalidNodeTypeException, NameInNamespaceNotFoundException, NodeNotFoundException, NoUserParameterException {
        //Check user can create an object attribute in the table oa
        Table table = insert.getTable();

        //get the columns the user has access to
        long columnsContId = pmManager.getEntityId(table.getName(), "Columns");
        List<Node> accColumns = pmManager.getAccessibleChildren(columnsContId, table.getName());
        List<String> accColumnNames = new ArrayList<>();
        for(Node node : accColumns) {
            accColumnNames.add(node.getName());
        }

        //check that each target column is in accColumns
        List<Column> targetColumns = insert.getColumns();
        for(Column column : targetColumns) {
            if(!accColumnNames.contains(column.getColumnName())) {
                throw new PMAccessDeniedException(column.getColumnName());
            }
        }


        //check they can write to each column

        return null;
    }

    /*Insert_Row_in_EmployeeTable(row, name, phone, ssn, salary)
    { CreateOAinOA(row, EmployeeTable)
        CreateOinOA(name, row)
        Assign(name, Name)
        CreateOinOA(phone, row)
        Assign(phone, Phone)
        CreateOinOA(ssn, row)
        Assign(ssn, SSN)
        CreateOinOA(salary, row)
        Assign(salary, Salary)
    }*/

    private boolean createOAinOA(long chidId, long parentId) {
        //create node
        //check can assign OA to OA
        return false;
    }
    private boolean createOinOA(long chidId, long parentId) {
        return false;
    }
    private boolean assignOAtoOA(long chidId, long parentId) {
        return false;
    }
    private boolean assignOtoOA(long chidId, long parentId) {
        return false;
    }
}
