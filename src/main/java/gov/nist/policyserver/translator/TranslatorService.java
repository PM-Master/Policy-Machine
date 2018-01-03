package gov.nist.policyserver.translator;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.service.AccessService;
import gov.nist.policyserver.service.NodeService;
import gov.nist.policyserver.translator.algorithms.*;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class TranslatorService {
    private NodeService   nodeService;
    private AccessService accessService;

    private Connection conn;
    private String     database;
    private Algorithm  algorithm;
    private PmManager  pmManager;
    private DbManager  dbManager;
    public TranslatorService() throws ConfigurationException {
        nodeService = new NodeService();
        accessService = new AccessService();
    }

    public String translate(String sql, String username, String host, int port,
                            String dbUsername, String dbPassword, String database)
            throws SQLException, IOException, ClassNotFoundException,
            JSQLParserException, PolicyMachineException, PMAccessDeniedException,
            NodeNotFoundException, NameInNamespaceNotFoundException,
            InvalidNodeTypeException, InvalidPropertyException, NoUserParameterException {
        pmManager = new PmManager(username, nodeService, accessService);

        dbManager = new DbManager();
        dbManager.setConnection(host, port, dbUsername, dbPassword);
        dbManager.setDatabase(database);

        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            algorithm = new SelectAlgorithm((Select) statement, pmManager, dbManager);
        } else if (statement instanceof Insert) {
            algorithm = new InsertAlgorithm((Insert) statement, pmManager, dbManager);
        } else if (statement instanceof Update) {
            algorithm = new UpdateAlgorithm((Update) statement, pmManager, dbManager);
        } else if (statement instanceof Delete) {
        }

        return algorithm.run();
    }
}
