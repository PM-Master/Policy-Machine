package gov.nist.policyserver.service;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;

import java.sql.*;
import java.util.*;

import static gov.nist.policyserver.common.Constants.COLUMN_INDEX_PROPERTY;
import static gov.nist.policyserver.common.Constants.NAMESPACE_PROPERTY;
import static gov.nist.policyserver.dao.DAO.getDao;

public class ConfigurationService {
    private PmGraph  graph;
    private PmAccess access;

    private NodeService nodeService;
    private AssignmentService assignmentService;

    public ConfigurationService() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();

        nodeService = new NodeService();
        assignmentService = new AssignmentService();
    }

    public void connect(String database, String host, int port, String schema, String username, String password) throws DatabaseException, ConfigurationException {
        Properties props = new Properties();
        props.put("database", database);
        props.put("host", host);
        props.put("port", String.valueOf(port));
        props.put("username", username);
        props.put("password", password);
        props.put("schema", schema == null ? "" : schema);
        DAO.init(props);
    }

    public void setInterval(int interval) throws ConfigurationException {
        DAO.setInterval(interval);
    }

    public void importData(String host, int port, String schema, String username, String password)
            throws DatabaseException, NodeNotFoundException, ConfigurationException, AssignmentExistsException,
            NullNameException, NodeNameExistsException, NodeNameExistsInNamespaceException, NullTypeException,
            InvalidPropertyException, InvalidNodeTypeException, PropertyNotFoundException {
        //create the schema policy class node
        Node schemaNode = nodeService.createNode(schema+"_PC", NodeType.PC.toString(), "Policy Class for " + schema, null);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema, username, password);
            Statement stmt = conn.createStatement();
            stmt.execute("use " + schema);
            ResultSet rs = stmt.executeQuery("show full tables");
            while(rs.next()){
                String tableName = rs.getString(1);

                //create table node
                Property[] properties = {
                        new Property(NAMESPACE_PROPERTY, tableName)
                };
                Node tableNode = nodeService.createNode(tableName, NodeType.OA.toString(), tableName, properties);

                //assign table node to policy class node
                assignmentService.createAssignment(tableNode.getId(), schemaNode.getId());

                //create columns
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery("describe " + tableName);
                String columnSql = "";
                String firstColumnName = "";
                int columnIndex = 0;
                while(rs1.next()){
                    String columnName = rs1.getString(1);
                    if(columnIndex == 0){
                        firstColumnName = columnName;
                        nodeService.addNodeProperty(tableNode.getId(), Constants.ORDER_BY_PROPERTY, firstColumnName);
                    }

                    properties = new Property[]{
                            new Property(NAMESPACE_PROPERTY, tableName),
                            new Property(COLUMN_INDEX_PROPERTY, String.valueOf(++columnIndex))
                    };
                    Node columnNode = nodeService.createNode(columnName, NodeType.OA.toString(), columnName, properties);

                    //assign column node to table
                    assignmentService.createAssignment(columnNode.getId(), tableNode.getId());

                    columnSql += columnName + ", ";
                }
                columnSql = columnSql.substring(0, columnSql.length()-2);

                //create rows
                if(!columnSql.isEmpty()){
                    //get data from table
                    String sql = "select " + columnSql + " from " + tableName + " order by " + tableNode.getProperty(Constants.ORDER_BY_PROPERTY) .getValue();
                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(sql);
                    int rowIndex = 0;
                    while(rs2.next()){
                        //creating rows
                        ResultSetMetaData metaData = rs2.getMetaData();
                        String rowName = UUID.randomUUID().toString();

                        //create row node
                        properties = new Property[]{
                                new Property(Constants.NAMESPACE_PROPERTY, tableName),
                                new Property(Constants.ROW_INDEX_PROPERTY, String.valueOf(++rowIndex))
                        };
                        Node rowNode = nodeService.createNode(rowName, NodeType.OA.toString(), rowName, properties);

                        //assign row node to table
                        assignmentService.createAssignment(rowNode.getId(), tableNode.getId());

                        //create data objects, assign to row and column
                        for(int i = 1; i <= metaData.getColumnCount(); i++){
                            //get column
                            HashSet<Node> nodes = nodeService.getNodes(tableName, null, NodeType.OA.toString(),
                                    Constants.COLUMN_INDEX_PROPERTY, String.valueOf(i));

                            //should only be one node found, this is the column
                            Node columnNode = nodes.iterator().next();


                            //create data object node
                            String objectName = UUID.randomUUID().toString();
                            properties = new Property[]{
                                    new Property(Constants.NAMESPACE_PROPERTY, tableName),
                                    new Property(Constants.ROW_INDEX_PROPERTY, String.valueOf(++rowIndex)),
                                    new Property(Constants.COLUMN_INDEX_PROPERTY, String.valueOf(i))
                            };
                            Node objectNode = nodeService.createNode(objectName, NodeType.O.toString(), "Object in table=" + tableName + ", row=" + rowName + ", column=" + columnNode.getName(), properties);

                            //assign object to row and column
                            assignmentService.createAssignment(objectNode.getId(), rowNode.getId());
                            assignmentService.createAssignment(objectNode.getId(), columnNode.getId());
                        }
                    }
                }
            }

        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            throw new DatabaseException(PmException.CLIENT_ERROR, e.getMessage());
        }
    }
}
