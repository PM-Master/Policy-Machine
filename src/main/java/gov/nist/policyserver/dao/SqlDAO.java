package gov.nist.policyserver.dao;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Assignment;
import gov.nist.policyserver.model.graph.relationships.Association;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Utility.arrayToString;
import static gov.nist.policyserver.common.Utility.setToString;

public class SqlDAO extends DAO {

    public SqlDAO() throws DatabaseException {
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + schema, username, password);
            System.out.println("Connected to MySQL");
        }catch(Exception e){
            throw new DatabaseException(e.hashCode(), e.getMessage());
        }
    }

    @Override
    public void buildGraph() throws DatabaseException {
        graph = new PmGraph();
        List<Node> nodes = getNodes();
        for (Node node : nodes) {
            if (!node.getType().equals(NodeType.OS)) {
                graph.addNode(node);
            }
        }

        List<Assignment> assignments = getAssignments();
        for (Assignment assignment : assignments) {
            Node start = assignment.getChild();
            Node end = assignment.getParent();
            if(graph.getNode(start.getId()) == null || graph.getNode(end.getId()) == null){
                continue;
            }
            graph.createAssignment(assignment.getChild(), assignment.getParent());
        }

        List<Association> associations = getAssociations();
        for (Association assoc : associations) {
            graph.createAssociation(assoc.getChild(), assoc.getParent(), assoc.getOps(), assoc.isInherit());
        }
    }

    @Override
    public void buildProhibitions() throws DatabaseException {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT deny_name, abbreviation, user_attribute_id, is_intersection, " +
                    "object_attribute_id, object_complement, " +
                    "get_operation_name(deny_operation_id)  " +
                    "FROM deny, deny_obj_attribute, deny_operation, deny_type " +
                    "WHERE deny.deny_id = deny_obj_attribute.deny_id " +
                    "AND deny.deny_id = deny_operation.deny_id " +
                    "AND deny_type.deny_type_id = deny.deny_type_id");

            while(rs.next()) {
                //prohibitions and subject information
                String deny_name = rs.getString(1);
                String type_abbr = rs.getString(2);
                int ua_id = rs.getInt(3);
                boolean intersection = rs.getInt(4) == 1;
                //resource information
                int object_attribute_id = rs.getInt(5);
                boolean object_complement = rs.getInt(6) == 1;
                //operation information
                String operation_name = rs.getString(7);

                Prohibition p = access.getProhibition(deny_name);
                if (p == null) {
                    ProhibitionSubject subject = new ProhibitionSubject(ua_id, ProhibitionSubjectType.toProhibitionSubjectType(type_abbr));
                    List<ProhibitionRes> resources = new ArrayList<>();
                    resources.add(new ProhibitionRes(object_attribute_id, object_complement));
                    HashSet<String> operations = new HashSet<>();
                    operations.add(operation_name);

                    p = new Prohibition(subject, resources, deny_name, operations, intersection);
                    access.addProhibition(p);
                } else {
                    boolean found = false;
                    List<ProhibitionRes> prs = p.getResources();
                    for (ProhibitionRes pr: prs) {
                        if (pr.getResourceId() == object_attribute_id) {
                            found = true;
                        }
                    }
                    if (!found) {
                        p.addResource(new ProhibitionRes(object_attribute_id, object_complement));
                    }
                    HashSet<String> ops = p.getOperations();
                    ops.add(operation_name);
                    p.setOperations(ops);
                }
            }
        } catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        } catch (InvalidProhibitionSubjectTypeException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    private List<Node> getNodes() throws DatabaseException {
        try {
            List<Node> nodes = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select node_id,name,node_type_id,description from node");
            while (rs.next()) {
                long id = rs.getInt(1);
                String name = rs.getString(2);
                NodeType type = NodeType.toNodeType(rs.getInt(3));
                String description = rs.getString(4);
                Node node = new Node(id, name, type, description);

                Statement propStmt = conn.createStatement();
                ResultSet propRs = propStmt.executeQuery("SELECT property_key, NODE_PROPERTY.property_value FROM NODE_PROPERTY WHERE PROPERTY_NODE_ID = " + id);
                while(propRs.next()){
                    String key = propRs.getString(1);
                    String value = propRs.getString(2);
                    Property prop = new Property(key, value);
                    node.addProperty(prop);
                }

                nodes.add(node);
            }
            return nodes;
        }catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }catch(InvalidNodeTypeException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }catch(InvalidPropertyException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public List<Assignment> getAssignments() throws DatabaseException {
        try{
            List<Assignment> relationships = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT start_node_id,a.name,a.node_type_id,end_node_id,b.name,b.node_type_id FROM assignment join node a on start_node_id = a.node_id join node b on end_node_id=b.node_id where assignment.depth=1;");
            while(rs.next()){
                long id = rs.getInt(1);
                String name = rs.getString(2);
                NodeType type = NodeType.toNodeType(rs.getInt(3));
                Node endNode = new Node(id, name, type);
                if(type.equals(NodeType.OS))continue;

                id = rs.getInt(4);
                name = rs.getString(5);
                type = NodeType.toNodeType(rs.getInt(6));
                Node startNode = new Node(id, name, type);
                if(type.equals(NodeType.OS))continue;

                relationships.add(new Assignment(startNode, endNode));
            }
            return relationships;
        }catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }catch(InvalidNodeTypeException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    private List<Association> getAssociations() throws DatabaseException {
        try{
            List<Association> associations = new ArrayList<>();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ua_id,a.name,a.node_type_id, get_operations(opset_id),oa_id,b.name,b.node_type_id FROM association join node a on ua_id = a.node_id join node b on oa_id=b.node_id");
            while (rs.next()) {
                long id = rs.getInt(1);
                String name = rs.getString(2);
                NodeType type = NodeType.toNodeType(rs.getInt(3));
                Node startNode = new Node(id, name, type);

                HashSet<String> ops = new HashSet<>(Arrays.asList(rs.getString(4).split(",")));

                id = rs.getInt(5);
                name = rs.getString(6);
                type = NodeType.toNodeType(rs.getInt(7));
                Node endNode = new Node(id, name, type);

                associations.add(new Association(startNode, endNode, ops, true));
            }
            return associations;
        }catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }catch(InvalidNodeTypeException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public Node createNode(long id, String name, NodeType type, String descr) throws DatabaseException {
        try{
            CallableStatement cs = conn.prepareCall("{? = call create_node_fun(?,?,?,?)}");
            cs.registerOutParameter(1, Types.INTEGER);
            cs.setLong(2, id);
            cs.setString(3, name);
            cs.setString(4, type.toString());
            cs.setString(5, descr);
            cs.execute();
            id = cs.getInt(1);

            return new Node(id, name, type, descr);
        }catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void updateNode(long nodeId, String name, String descr) throws DatabaseException {
        try {
            if(name != null && !name.isEmpty()) {
                String sql = "update node set name='" + name + "' where node_id = " + nodeId  ;
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            }

            if(descr != null && !descr.isEmpty()) {
                String sql = "update node set description='" + name + "' where node_id = " + nodeId  ;
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            }
        }catch(SQLException e){
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteNode(long nodeId) throws DatabaseException {
        try{
            String sql = "delete from node where node_id=" + nodeId;
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void addNodeProperty(long nodeId, Property property) throws DatabaseException {
        try{
            String sql = "insert into node_property values (" + nodeId + ", '" + property.getKey() + "', '" + property.getValue() + "')";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteNodeProperty(long nodeId, String key) throws DatabaseException {
        int deletedRows = 0;
        try{
            Node node = graph.getNode(nodeId);
            String sql = "delete from node_property where property_node_id=" + nodeId + " and property_key='" + key + "'";
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            deletedRows = stmt.executeUpdate(sql);
            /*if (deletedRows==0) {
                throw new CustomException("Property not found");
            }*/
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void createAssignment(long childId, long parentId) throws DatabaseException {
        boolean result;
        try {
            CallableStatement stmt = conn.prepareCall("{call create_assignment(?,?,?)}");
            stmt.setInt(1, (int) parentId);
            stmt.setInt(2, (int) childId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            result = stmt.execute();
            String errorMsg = stmt.getString(3);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteAssignment(long childId, long parentId) throws DatabaseException {
        boolean result;
        try {
            CallableStatement stmt = conn.prepareCall("{call delete_assignment(?,?,?)}");

            stmt.setLong(1, parentId);
            stmt.setLong(2, childId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            result = stmt.execute();
            String errorMsg = stmt.getString(3);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void createAssociation(long uaId, long targetId, HashSet<String> operations, boolean inherit) throws DatabaseException {
        String ops = "";
        boolean result;
        for(String op : operations){
            ops += op + ",";
        }
        ops = ops.substring(0, ops.length()-1);
        try {
            System.out.println("Calling Create_Association Procedure");
            CallableStatement stmt = conn.prepareCall("{call create_association(?,?,?,?)}");
            stmt.setLong(1, uaId);
            stmt.setLong(2, targetId);
            stmt.setString(3, ops);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.execute();
            String errorMsg = stmt.getString(4);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void updateAssociation(long uaId, long targetId, boolean inherit, HashSet<String> operations) throws DatabaseException {
        String ops = "";
        for(String op : operations){
            ops += op + ",";
        }
        ops = ops.substring(0, ops.length()-1);
        try {
            CallableStatement stmt = conn.prepareCall("{? = call update_opset(?,?,?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setLong(2, uaId);
            stmt.setLong(3, targetId);
            stmt.setString(4, ops);
            stmt.execute();
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteAssociation(long uaId, long targetId) throws DatabaseException {
        boolean result;
        try {
            CallableStatement stmt = conn.prepareCall("{call delete_association(?,?,?)}");
            stmt.setLong(1, uaId);
            stmt.setLong(2, targetId);
            stmt.registerOutParameter(3, Types.VARCHAR);
            result = stmt.execute();
            String errorMsg = stmt.getString(3);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void createProhibition(String prohibitionName, HashSet<String> operations, boolean intersection, ProhibitionRes[] resources, ProhibitionSubject subject) throws DatabaseException{
        String[] resourceCompements = new String[resources.length];
        String resourceCompementsStr = "";
        String operationsStr = "";
        int i=0;
        boolean result;

        for (ProhibitionRes dr : resources) {
            resourceCompements[i++] = String.valueOf(dr.getResourceId()) + "-" + String.valueOf(dr.isCompliment());
        }

        resourceCompementsStr = arrayToString(resourceCompements, ",");
        operationsStr = setToString(operations, ",");
        System.out.println(operationsStr);
        System.out.println(resourceCompementsStr);
        try{
            CallableStatement stmt = conn.prepareCall("{call create_deny(?,?,?,?,?,?,?,?)}");
            stmt.setString(1, prohibitionName);
            stmt.setString(2, subject.getSubjectType().toString());
            stmt.setString(3, operationsStr);
            stmt.setBoolean(4, intersection);
            stmt.setString(5, resourceCompementsStr);
            if (subject.getSubjectType().toString().equalsIgnoreCase("u") || subject.getSubjectType().toString().equalsIgnoreCase("ua")) {
                stmt.setString(6, String.valueOf(subject.getSubjectId()));
                stmt.setString(7, null);
            } else {
                stmt.setString(6, null);
                stmt.setString(7, String.valueOf(subject.getSubjectId()));
            }
            stmt.registerOutParameter(8, Types.VARCHAR);
            result = stmt.execute();
            String errorMsg = stmt.getString(8);
            if (errorMsg!= null && errorMsg.length() > 0) {
                throw new DatabaseException(2000, errorMsg);
            }

        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    //TODO: Need to test
    @Override
    public synchronized void deleteProhibition(String prohibitionName) throws DatabaseException {
        try {
            String sql = "DELETE FROM deny WHERE UPPER(deny_name) = UPPER('" + prohibitionName + "')";
            Statement stmt = conn.createStatement();
            int affectedRows = stmt.executeUpdate(sql);
            if (affectedRows <=0 ) {
                throw new DatabaseException(8000, "Error deleting prohibition " + prohibitionName);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void addResourceToProhibition(String prohibitionName, long resourceId, boolean compliment) throws DatabaseException {
        try {
            String sql = "INSERT INTO deny_obj_attribute VALUES ((SELECT deny_id FROM deny WHERE UPPER(deny_name) = UPPER('" + prohibitionName + "')), " + resourceId + ", " + (compliment ? 1 : 0) + ");";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void deleteProhibitionResource(String prohibitionName, long resourceId) throws DatabaseException {
        try {
            String sql = "DELETE FROM deny_obj_attribute WHERE deny_id = (SELECT deny_id FROM deny WHERE UPPER(deny_name) = UPPER('" + prohibitionName + "')) AND object_attribute_id = " + resourceId;
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            int rowsAffected = stmt.executeUpdate(sql);
            if (rowsAffected == 0) {
                throw new DatabaseException(8000, "Error deleting resource for prohibition " + prohibitionName);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void setProhibitionSubject(String prohibitionName, long subjectId, ProhibitionSubjectType subjectType) throws DatabaseException {
        try {
            String sql = "UPDATE deny SET user_attribute_id = " + subjectId + " WHERE deny_name = '" + prohibitionName + "' AND " + "deny_type_id = (SELECT deny_type_id FROM deny_type WHERE abbreviation = '" + subjectType.toString() + "')";
            System.out.println(sql);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public synchronized void setProhibitionOperations(String prohibitionName, HashSet<String> ops) throws DatabaseException {
        try{
            //first, delete all entries
            String sql = "DELETE FROM deny_operation WHERE deny_id = (SELECT deny_id FROM deny WHERE UPPER(deny_name) = UPPER('" + prohibitionName + "'))";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

            //set the operations
            String oSql = "";
            for (String o : ops) {
                oSql += "((SELECT deny_id FROM deny WHERE UPPER(deny_name) = UPPER('" + prohibitionName + "')), " + "(SELECT operation_id FROM operation WHERE UPPER(name) = UPPER('" + o + "'))), ";
            }
            oSql = oSql.substring(0, oSql.length() - 2);

            sql = "INSERT INTO deny_operation VALUES " + oSql + ";";
            stmt = conn.createStatement();
            stmt.execute(sql);
        }
        catch (SQLException e) {
            throw new DatabaseException(e.getErrorCode(), e.getMessage());
        }
    }

    @Override
    public void createSession(long id, long id1) throws DatabaseException {

    }

    @Override
    public Node getSessionUser(String sessionId) throws DatabaseException {
        return null;
    }
}
