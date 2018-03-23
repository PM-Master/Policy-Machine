package gov.nist.policyserver.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Assignment;

import java.sql.*;
import java.util.*;

import gov.nist.policyserver.model.graph.relationships.Association;

import static gov.nist.policyserver.dao.DAO.getDao;

public class ConfigurationService extends Service{
    private NodeService nodeService;
    private AssignmentService assignmentService;
    private AccessService accessService;

    public ConfigurationService() throws ConfigurationException {
        super();

        nodeService = new NodeService();
        assignmentService = new AssignmentService();
        accessService = new AccessService();
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
            InvalidPropertyException, InvalidNodeTypeException, PropertyNotFoundException, NameInNamespaceNotFoundException {
        //create the schema policy class node
        Property[] properties = new Property[] {
                new Property(Constants.SCHEMA_COMP_PROPERTY, Constants.SCHEMA_COMP_SCHEMA_PROPERTY),
                };
        Node pcNode = createNode(schema, NodeType.PC.toString(), "Policy Class for " + schema, properties);

        // create the schema object attribute node
        Node schemaNode = createNode(schema, NodeType.OA.toString(), "Base Object Attribute for " + schema, properties);

        //assign oa node to pc node
        assignmentService.createAssignment(schemaNode.getId(), pcNode.getId());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema, username, password);
            Statement stmt = conn.createStatement();
            stmt.execute("use " + schema);
            ResultSet rs = stmt.executeQuery("show full tables where Table_Type = 'BASE TABLE'");
            while(rs.next()){
                List<String> keys = new ArrayList<>();

                String tableName = rs.getString(1);

                //get primary keys to make name
                PreparedStatement ps2 = conn.prepareStatement("SELECT k.COLUMN_NAME\n" +
                        "FROM information_schema.table_constraints t\n" +
                        "LEFT JOIN information_schema.key_column_usage k\n" +
                        "USING(constraint_name,table_schema,table_name)\n" +
                        "WHERE t.constraint_type='PRIMARY KEY'\n" +
                        "    AND t.table_schema=DATABASE()\n" +
                        "    AND t.table_name='" + tableName + "' order by ordinal_position;");
                ResultSet rs3 = ps2.executeQuery();
                while(rs3.next()){
                    keys.add(rs3.getString(1));
                }

                //create table node
                properties = new Property[] {
                        new Property(Constants.SCHEMA_NAME_PROPERTY, schema),
                        new Property(Constants.NAMESPACE_PROPERTY, tableName),
                        new Property(Constants.SCHEMA_COMP_PROPERTY, Constants.SCHEMA_COMP_TABLE_PROPERTY)
                };
                Node tableNode = createNode(tableName, NodeType.OA.toString(), tableName, properties);

                //assign table node to policy class node
                assignmentService.createAssignment(tableNode.getId(), schemaNode.getId());

                //create columns container
                properties = new Property[] {
                        new Property(Constants.NAMESPACE_PROPERTY, tableName)
                };
                Node columnsNode = createNode(Constants.COLUMN_CONTAINER_NAME, NodeType.OA.toString(),
                        "Column container for " + tableName, properties);
                assignmentService.createAssignment(columnsNode.getId(), tableNode.getId());

                //create columns
                Statement stmt1 = conn.createStatement();
                String colSql = "SELECT c.column_name FROM INFORMATION_SCHEMA.COLUMNS c WHERE c.table_name = '" + tableName + "' AND c.table_schema = '" + schema + "'";
                ResultSet rs1 = stmt1.executeQuery(colSql);
                String columnSql = "";
                while(rs1.next()){
                    String columnName = rs1.getString(1);
                    System.out.println("creating column " + columnName);

                    properties = new Property[]{
                            new Property(Constants.NAMESPACE_PROPERTY, tableName)
                    };
                    Node columnNode = createNode(columnName, NodeType.OA.toString(), columnName, properties);

                    //assign column node to table
                    assignmentService.createAssignment(columnNode.getId(), columnsNode.getId());

                    columnSql += columnName + ", ";
                }
                columnSql = columnSql.substring(0, columnSql.length()-2);

                //create rows
                if(!columnSql.isEmpty()){
                    //create rows containers
                    properties = new Property[] {
                            new Property(Constants.NAMESPACE_PROPERTY, tableName)
                    };
                    Node rowsNode = createNode(Constants.ROW_CONTAINER_NAME, NodeType.OA.toString(),
                            "Row container for " + tableName, properties);
                    assignmentService.createAssignment(rowsNode.getId(), tableNode.getId());

                    //get data from table
                    String sql = "select " + columnSql + " from " + tableName;
                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(sql);
                    ResultSetMetaData rs2MetaData = rs2.getMetaData();
                    int numCols = rs2MetaData.getColumnCount();
                    while(rs2.next()){
                        //creating rows
                        String rowName = "";
                        for(int i = 1; i <= numCols; i++){
                            String columnName = rs2MetaData.getColumnName(i);
                            if(keys.contains(columnName)){
                                String value = String.valueOf(rs2.getObject(i));
                                if(rowName.isEmpty()){
                                    rowName += value;
                                }else{
                                    rowName += "+" + value;
                                }
                            }
                        }
                        System.out.println("creating row " + rowName);


                        //create row node
                        properties = new Property[]{
                                new Property(Constants.NAMESPACE_PROPERTY, tableName),
                                new Property(Constants.SCHEMA_COMP_PROPERTY, Constants.SCHEMA_COMP_ROW_PROPERTY)
                        };
                        Node rowNode = createNode(rowName, NodeType.OA.toString(), rowName, properties);

                        //assign row node to table
                        assignmentService.createAssignment(rowNode.getId(), rowsNode.getId());

                        //create data objects, assign to row and column
                        for(int i = 1; i <= rs2MetaData.getColumnCount(); i++){
                            //get column
                            String columnName = rs2MetaData.getColumnName(i);
                            Node columnNode = nodeService.getNodeInNamespace(tableName, columnName);


                            //create data object node
                            String objectName = UUID.randomUUID().toString();
                            properties = new Property[]{
                                    new Property(Constants.NAMESPACE_PROPERTY, tableName)
                            };
                            Node objectNode = createNode(objectName, NodeType.O.toString(), "Object in table=" + tableName + ", row=" + rowName + ", column=" + columnNode.getName(), properties);

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

    private Node createNode(String name, String type, String description, Property[] properties) throws DatabaseException, InvalidPropertyException, ConfigurationException, InvalidNodeTypeException {
        //create node in database
        NodeType nt = NodeType.toNodeType(type);
        Node newNode = getDao().createNode(0, name, nt, description);

        //add the node to the nodes
        graph.addNode(newNode);

        //add properties to the node
        if(properties != null) {
            for (Property property : properties) {
                try {
                    nodeService.addNodeProperty(newNode.getId(), property.getKey(), property.getValue());
                }
                catch (NodeNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return newNode;
    }

    public Table getData(String host, int port, String username, String password, String database, String tableName) throws InvalidNodeTypeException, InvalidPropertyException, PropertyNotFoundException, NodeNotFoundException, NameInNamespaceNotFoundException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

            //get table node
            Node tableNode = nodeService.getNodeInNamespace(tableName, tableName);

            Table table = new Table();
            table.setNode(tableNode);

            HashSet<Node> children = nodeService.getChildrenOfType(tableNode.getId(), NodeType.OA.toString());

            Node columnsNode = null;
            Node rowsNode = null;
            for(Node node : children){
                if(node.getName().equals(Constants.COLUMN_CONTAINER_NAME)){
                    columnsNode = node;
                }else if(node.getName().equals(Constants.ROW_CONTAINER_NAME)){
                    rowsNode = node;
                }
            }

            if(columnsNode == null || rowsNode == null){
                throw new NodeNotFoundException(columnsNode == null ? Constants.COLUMN_CONTAINER_NAME : Constants.ROW_CONTAINER_NAME);
            }

            //get column Nodes
            HashSet<Node> columnNodes = nodeService.getChildrenOfType(columnsNode.getId(), NodeType.OA.toString());

            List<Column> columns = new ArrayList<>();
            String cols = "";
            for(Node col : columnNodes){
                Column column = new Column(col, col.getName());
                columns.add(column);

                if(cols.isEmpty()){
                    cols += col.getName();
                }else{
                    cols += "," + col.getName();
                }
            }

            table.setColumns(columns);

            //get table keys
            List<String> keys = new ArrayList<>();
            PreparedStatement ps2 = conn.prepareStatement("SELECT k.COLUMN_NAME\n" +
                    "FROM information_schema.table_constraints t\n" +
                    "LEFT JOIN information_schema.key_column_usage k\n" +
                    "USING(constraint_name,table_schema,table_name)\n" +
                    "WHERE t.constraint_type='PRIMARY KEY'\n" +
                    "    AND t.table_schema=DATABASE()\n" +
                    "    AND t.table_name='" + tableName + "';");
            ResultSet rs3 = ps2.executeQuery();
            while(rs3.next()){
                keys.add(rs3.getString(1));
            }

            //get all row nodes
            HashSet<Node> rowNodes = nodeService.getChildrenOfType(rowsNode.getId(), NodeType.OA.toString());

            //get row values
            String select = "select " + cols + " from " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(select);
            List<Row> rows = new ArrayList<>();
            int rowIndex = 0;
            int numCols = rs.getMetaData().getColumnCount();
            while(rs.next()){
                Row row = new Row();
                String rowName = "";
                List<Object> rowValues = new ArrayList<>();
                for(int i = 1; i <= numCols; i++){
                    //add row value
                    String value = String.valueOf(rs.getObject(i));
                    rowValues.add(rs.getObject(i));

                    //get column name
                    String columnName = rs.getMetaData().getColumnName(i);

                    //construct rowName
                    if(keys.contains(columnName)){
                        if(rowName.isEmpty()){
                            rowName += value;
                        }else{
                            rowName += "+" + value;
                        }
                    }
                }
                row.setRowValues(rowValues);

                //get row node
                Node rowNode = null;
                for(Node rN : rowNodes){
                    if(rN.getName().equals(rowName)){
                        rowNode = rN;
                    }
                }

                if(rowNode == null){
                    throw new NodeNotFoundException(rowName);
                }

                row.setNode(rowNode);

                //now, get the objects that intersect the current row and columns
                List<Node> rowNodesList = new ArrayList<>();
                for(int i = 1; i <= numCols; i++) {
                    //get column name
                    String columnName = rs.getMetaData().getColumnName(i);

                    //get columnNode
                    Node columnNode = nodeService.getNodeInNamespace(tableName, columnName);

                    HashSet<Node> colChildren = nodeService.getChildrenOfType(columnNode.getId(), NodeType.O.toString());
                    HashSet<Node> rowChildren = nodeService.getChildrenOfType(rowNode.getId(), NodeType.O.toString());

                    colChildren.retainAll(rowChildren);

                    rowNodesList.add(colChildren.iterator().next());
                }

                row.setRowNodes(rowNodesList);

                rows.add(row);
            }
            table.setRows(rows);

            return table;
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    class JsonGraph {
        HashSet<Node> nodes;
        HashSet<JsonAssignment> assignments;
        HashSet<JsonAssociation> associations;

        public JsonGraph(HashSet<Node> nodes, HashSet<JsonAssignment> assignments, HashSet<JsonAssociation> associations) {
            this.nodes = nodes;
            this.assignments = assignments;
            this.associations = associations;
        }

        public HashSet<Node> getNodes() {
            return nodes;
        }

        public HashSet<JsonAssignment> getAssignments() {
            return assignments;
        }

        public HashSet<JsonAssociation> getAssociations() {
            return associations;
        }
    }

    class JsonAssignment {
        long child;
        long parent;

        public JsonAssignment(long child, long parent) {
            this.child = child;
            this.parent = parent;
        }

        public long getChild() {
            return child;
        }

        public long getParent() {
            return parent;
        }
    }

    class JsonAssociation {
        long ua;
        long target;
        HashSet<String> ops;
        boolean isInherit;

        public JsonAssociation(long ua, long target, HashSet<String> ops, boolean isInherit) {
            this.ua = ua;
            this.target = target;
            this.ops = ops;
            this.isInherit = isInherit;
        }

        public long getUa() {
            return ua;
        }

        public long getTarget() {
            return target;
        }

        public HashSet<String> getOps() {
            return ops;
        }

        public boolean isInherit() {
            return isInherit;
        }
    }

    public String save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        HashSet<Node> nodes = graph.getNodes();
        for(Node node : nodes) {
            List<Property> properties = node.getProperties();
            for(Property prop : properties) {
                if(prop.getKey().equals("description")) {
                    properties.remove(prop);
                    break;
                }
            }
        }
        HashSet<Assignment> assignments = graph.getAssignments();
        HashSet<JsonAssignment> jsonAssignments = new HashSet<>();
        for(Assignment assignment : assignments) {
            jsonAssignments.add(new JsonAssignment(assignment.getChild().getId(), assignment.getParent().getId()));
        }

        List<Association> associations = graph.getAssociations();
        HashSet<JsonAssociation> jsonAssociations = new HashSet<>();
        for(Association association : associations) {
            jsonAssociations.add(new JsonAssociation(association.getChild().getId(), association.getParent().getId(), association.getOps(), association.isInherit()));
        }

        return gson.toJson(new JsonGraph(nodes, jsonAssignments, jsonAssociations));
    }

    public void load(String config) throws NullNameException, NodeNameExistsException, NodeNameExistsInNamespaceException, DatabaseException, NullTypeException, InvalidPropertyException, ConfigurationException, InvalidNodeTypeException, NodeNotFoundException, AssignmentExistsException, NodeIdExistsException {
        JsonGraph graph = new Gson().fromJson(config, JsonGraph.class);

        HashSet<Node> nodes = graph.getNodes();
        for(Node node : nodes) {
            List<Property> properties = node.getProperties();
            Property[] propArr = null;
            if(properties != null) {
                propArr = new Property[properties.size()];
                propArr = properties.toArray(propArr);
            }
            nodeService.createNode(node.getId(), node.getName(), node.getType().toString(), node.getDescription(), propArr);
        }

        HashSet<JsonAssignment> assignments = graph.getAssignments();
        for(JsonAssignment assignment : assignments) {
            // child - assigned to -> parent
            if(assignment != null) {
                assignmentService.createAssignment(assignment.getChild(), assignment.getParent());
            }
        }

        HashSet<JsonAssociation> associations = graph.getAssociations();
        for(JsonAssociation association : associations) {
            accessService.grantAccess(association.getUa(), association.getTarget(), association.getOps(), association.isInherit());
        }
    }

    public JsonNode getGraph() throws InvalidNodeTypeException, InvalidPropertyException, NodeNotFoundException {
        HashSet<Node> cNodes = nodeService.getNodes(null, null, "C", null, null);
        Node cNode = cNodes.iterator().next();
        JsonNode root = new JsonNode((int)cNode.getId(), cNode.getName(), "C", "Connector Node", cNode.getProperties(), getJsonNodes(cNode.getId()));
        return root;
    }
    public JsonNode getUserGraph() throws InvalidNodeTypeException, InvalidPropertyException, NodeNotFoundException {
        System.out.println("in get user graph");
        HashSet<Node> cNodes = nodeService.getNodes(null, null, "C", null, null);
        Node cNode = cNodes.iterator().next();
        JsonNode root = new JsonNode((int)cNode.getId(), cNode.getName(), "C", "Connector Node", cNode.getProperties(), getJsonUserNodes(cNode.getId()));
        return root;
    }
    public JsonNode getObjGraph() throws InvalidNodeTypeException, InvalidPropertyException, NodeNotFoundException {
        HashSet<Node> cNodes = nodeService.getNodes(null, null, "C", null, null);
        Node cNode = cNodes.iterator().next();
        JsonNode root = new JsonNode((int)cNode.getId(), cNode.getName(), "C", "Connector Node", cNode.getProperties(), getJsonObjNodes(cNode.getId()));
        return root;
    }

    List<JsonNode> getJsonUserNodes(long id) throws NodeNotFoundException, InvalidNodeTypeException {
        System.out.println("in get user nodes");
        List<JsonNode> jsonNodes = new ArrayList<>();
        HashSet<Node> children = nodeService.getChildrenOfType(id, "PC");
        children.addAll(nodeService.getChildrenOfType(id, "U"));
        children.addAll(nodeService.getChildrenOfType(id, "UA"));
        for(Node node : children) {
            jsonNodes.add(new JsonNode(
                    node.getId(),
                    node.getName(),
                    node.getType().toString(),
                    node.getDescription(),
                    node.getProperties(),
                    getJsonNodes(node.getId())));
        }

        if(jsonNodes.isEmpty()){
            return null;
        }else {
            return jsonNodes;
        }
    }

    List<JsonNode> getJsonObjNodes(long id) throws NodeNotFoundException, InvalidNodeTypeException {
        List<JsonNode> jsonNodes = new ArrayList<>();
        HashSet<Node> children = nodeService.getChildrenOfType(id, "PC");
        children.addAll(nodeService.getChildrenOfType(id, "O"));
        children.addAll(nodeService.getChildrenOfType(id, "OA"));
        for(Node node : children) {
            jsonNodes.add(new JsonNode(
                    node.getId(),
                    node.getName(),
                    node.getType().toString(),
                    node.getDescription(),
                    node.getProperties(),
                    getJsonNodes(node.getId())));
        }

        if(jsonNodes.isEmpty()){
            return null;
        }else {
            return jsonNodes;
        }
    }

    class graph {
        HashSet<Node> nodes;
        HashSet<Assignment> links;

        public graph(HashSet<Node> nodes, HashSet<Assignment> links) {
            this.nodes = nodes;
            this.links = links;
        }

        public HashSet<Node> getNodes() {
            return nodes;
        }

        public void setNodes(HashSet<Node> nodes) {
            this.nodes = nodes;
        }

        public HashSet<Assignment> getLinks() {
            return links;
        }

        public void setLinks(HashSet<Assignment> links) {
            this.links = links;
        }
    }

    List<JsonNode> getJsonNodes(long id) throws NodeNotFoundException, InvalidNodeTypeException {
        List<JsonNode> jsonNodes = new ArrayList<>();
        HashSet<Node> children = nodeService.getChildrenOfType(id, null);
        for(Node node : children) {
            jsonNodes.add(new JsonNode(
                    node.getId(),
                    node.getName(),
                    node.getType().toString(),
                    node.getDescription(),
                    node.getProperties(),
                    getJsonNodes(node.getId())));
        }

        if(jsonNodes.isEmpty()){
            return null;
        }else {
            return jsonNodes;
        }
    }

    class JsonNode {
        String     id;
        long       nodeId;
        String     name;
        String     type;
        String     description;
        List<Property> properties;
        List<JsonNode> children;

        public JsonNode(long id, String name, String type, String description, List<Property> properties, List<JsonNode> children) {
            //this.id = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
            this.id = new Integer((int)id).toString();
            this.nodeId = id;
            this.name = name;
            this.type = type;
            this.description = description;
            this.properties = properties;
            this.children = children;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Property> getProperties() {
            return properties;
        }

        public void setProperties(List<Property> properties) {
            this.properties = properties;
        }

        public List<JsonNode> getChildren() {
            return children;
        }

        public void setChildren(List<JsonNode> children) {
            this.children = children;
        }
    }

    public class Table{
        Node node;
        List<Column> columns;
        List<Row> rows;

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public List<Column> getColumns() {
            return columns;
        }

        public void setColumns(List<Column> columns) {
            this.columns = columns;
        }

        public List<Row> getRows() {
            return rows;
        }

        public void setRows(List<Row> rows) {
            this.rows = rows;
        }
    }

    class Column{
        Node node;
        String column;

        public Column(Node node, String column){
            this.node = node;
            this.column = column;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }
    }

    class Row{
        Node node;
        List<Node> rowNodes;
        List<Object> rowValues;

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public List<Node> getRowNodes() {
            return rowNodes;
        }

        public void setRowNodes(List<Node> rowNodes) {
            this.rowNodes = rowNodes;
        }

        public List<Object> getRowValues() {
            return rowValues;
        }

        public void setRowValues(List<Object> rowValues) {
            this.rowValues = rowValues;
        }
    }
}