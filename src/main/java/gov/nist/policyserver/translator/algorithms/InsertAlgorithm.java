package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class InsertAlgorithm extends Algorithm{
    private Insert insert;

    public InsertAlgorithm(Insert insert, PmManager pm, DbManager db) {
        super(pm, db);
        this.insert = insert;
    }

    @Override
    public String run() throws SQLException, IOException, PolicyMachineException, PMAccessDeniedException, JSQLParserException {
        //Check user can create an object attribute in the table oa
        Table table = insert.getTable();

        List<String> res = null;//pmManager.sendReceive(PmManager.GET_ENTITY_ID, pmTableName, "b");
        if(res == null || res.isEmpty()){
            throw new PolicyMachineException("Table " + table.getName() + " does not exist in the PM");
        }

        String pmTableId = res.get(0);
        List<String> perms = null;//pmManager.sendReceive(PmManager.GET_PERMITTED_OPS, pmManager.getPmUser(), pmTableId);
        System.out.println(perms);

        //check they can write to each column

        return null;
    }
}
