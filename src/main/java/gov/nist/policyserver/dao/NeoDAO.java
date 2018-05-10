package gov.nist.policyserver.dao;

import gov.nist.policyserver.exceptions.DatabaseException;
import gov.nist.policyserver.exceptions.PmException;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.helpers.JsonHelper;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Assignment;
import gov.nist.policyserver.model.graph.relationships.Association;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionResource;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Constants.ERR_NEO;

/**
 * Helper class for Neo4j
 */
public class NeoDAO extends DAO {
    
    private static String PROHIBITION_LABEL = "prohibition";
    
    public NeoDAO() throws DatabaseException {

    }

    /**
     * execute a cypher query
     *
     * @param cypher the query
     * @return the result of executing the query
     * @throws PmException
     */
    public ResultSet execute(String cypher) throws DatabaseException {
        try {
            PreparedStatement stmt = conn.prepareStatement(cypher);
            return stmt.executeQuery();
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            Driver driver = new org.neo4j.jdbc.Driver();
            DriverManager.registerDriver(driver);
            conn = DriverManager.getConnection("jdbc:neo4j:http://" + host + ":" + port + "", username, password);

            //load nodes into cache
            //warmUp();

            System.out.println("Connected to Neo4j");
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    @Override
    public void buildGraph() throws DatabaseException {
        graph = new PmGraph();

        System.out.println("\tGetting nodes...");
        List<Node> nodes = getNodes();
        for(Node node : nodes){
            graph.addNode(node);
        }

        System.out.println("\tGetting assignments...");
        List<Assignment> assignments = getAssignments();
        for(Assignment assignment : assignments){
            graph.createAssignment(graph.getNode(assignment.getChild().getId()), graph.getNode(assignment.getParent().getId()));
        }

        System.out.println("\tGetting associations...");
        List<Association> associations = getAssociations();
        for(Association assoc : associations){
            graph.createAssociation(assoc.getChild(), assoc.getParent(), assoc.getOps(), assoc.isInherit());
        }
    }

    @Override
    public void buildProhibitions() throws DatabaseException {
        String cypher = "match(d:D)<-[" + PROHIBITION_LABEL +"]-(s)\n" +
                "with d, s\n" +
                "match(d:D)-[" + PROHIBITION_LABEL +"]->(r)\n" +
                "return s, d, collect(r)";
        ResultSet rs = execute(cypher);
        try {
            while (rs.next()) {
                String json = rs.getString(1);
                ProhibitionSubject ps = JsonHelper.getProhibitionSubject(json);

                json = rs.getString(2);
                Prohibition prohibition = JsonHelper.getProhibition(json);

                json = rs.getString(3);
                List<ProhibitionResource> prs = JsonHelper.getProhibitionResources(json);

                prohibition.setResources(prs);
                prohibition.setSubject(ps);

                access.addProhibition(prohibition);
            }
        }catch(SQLException e){
            throw new DatabaseException(ERR_NEO, "Error getting prohibitions from nodes");
        }
    }

    private List<Node> getNodes() throws DatabaseException {
        String cypher = "MATCH (n) return n";
        ResultSet rs = execute(cypher);
        List<Node> nodes = getNodesFromResultSet(rs);
        for(Node node : nodes){
            node.setProperties(getNodeProps(node));
        }

        return nodes;
    }

    private List<Property> getNodeProps(Node node) throws DatabaseException {
        String cypher = "match(n:" + node.getType() + "{id:" + node.getId() + "}) return n";
        ResultSet rs = execute(cypher);
        try {
            List<Property> props = new ArrayList<>();
            while(rs.next()){
                String json = rs.getString(1);
                props.addAll(JsonHelper.getPropertiesFromJson(json));
            }
            return props;
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    private List<Association> getAssociations() throws DatabaseException {
        List<Association> associations = new ArrayList<>();

        String cypher = "match(ua:UA)-[a:association]->(oa:OA) return ua,oa,a.operations,a.inherit;";
        ResultSet rs = execute(cypher);
        try {
            while (rs.next()) {
                Node startNode = JsonHelper.getNodeFromJson(rs.getString(1));
                Node endNode = JsonHelper.getNodeFromJson(rs.getString(2));
                HashSet<String> ops = JsonHelper.getOpsFromJson(rs.getString(3));
                boolean inherit = Boolean.valueOf(rs.getString(4));
                Association assoc = new Association(startNode, endNode, ops, inherit);
                associations.add(assoc);
            }
            return associations;
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    @Override
    public List<Assignment> getAssignments() throws DatabaseException {
        List<Assignment> assignments = new ArrayList<>();

        String cypher = "match(n)-[r:assigned_to]->(m) return n, r, m";
        ResultSet rs = execute(cypher);
        try {
            while (rs.next()) {
                Node startNode = JsonHelper.getNodeFromJson(rs.getString(1));
                Node endNode = JsonHelper.getNodeFromJson(rs.getString(3));
                assignments.add(new Assignment(startNode, endNode));
            }
            return assignments;
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    private void warmUp() throws DatabaseException {
        execute("call apoc.warmup.run();");
    }

    @Override
    public Node createNode(long id, String name, NodeType type) throws DatabaseException {
        if(id == 0) {
            id = getMaxId() + 1;
        }
        String cypher = "CREATE " +
                "(n:" + type +
                "{" +
                "id: " + id + ", " +
                "name:'" + name + "'," +
                "type:'" + type + "'})";
        execute(cypher);

        return new Node(id, name, type);
    }

    public long getMaxId() throws DatabaseException {
        String cypher = "match(n) return max(n.id)";
        try {
            ResultSet rs = execute(cypher);
            rs.next();
            long maxId = rs.getLong(1);
            if(maxId == -1) {
                maxId = 1;
            }
            return maxId;
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }
    }

    @Override
    public void updateNode(long nodeId, String name) throws DatabaseException {
        if(name != null && !name.isEmpty()) {
            //update name
            String cypher = "merge (n {id:" + nodeId + "}) set n.name='" + name + "'";
            execute(cypher);
        }
    }

    @Override
    public void deleteNode(long nodeId) throws DatabaseException {
        //delete node
        String cypher = "MATCH (n) where n.id=" + nodeId + " DETACH DELETE n";
        execute(cypher);
    }

    @Override
    public void addNodeProperty(long nodeId, Property property) throws DatabaseException {
        String cypher = "match(n{id:" + nodeId + "}) set n." + property.getKey() + "='" + property.getValue() + "'";
        execute(cypher);
    }

    @Override
    public void deleteNodeProperty(long nodeId, String key) throws DatabaseException {
        String cypher = "match(n{id:" + nodeId + "}) remove n." + key;
        execute(cypher);
    }

    @Override
    public void updateNodeProperty(long nodeId, String key, String value) throws DatabaseException {
        String cypher = "match(n{id:" + nodeId + "}) set n." + key + " = '" + value + "'";
        execute(cypher);
    }

    private String setToCypherArray(HashSet<String> list) {
        String str = "[";
        for (String op : list) {
            op = "'" + op + "'";
            if (str.length()==1) {
                str += op;
            }
            else {
                str += "," + op;
            }
        }
        str += "]";
        return str;
    }

    private List<Node> getNodesFromResultSet(ResultSet rs) throws DatabaseException {
        List<Node> nodes = new ArrayList<>();

        try {
            while (rs.next()) {
                Node node = JsonHelper.getNodeFromJson(rs.getString(1));
                nodes.add(node);
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(ERR_NEO, e.getMessage());
        }

        return nodes;
    }

    @Override
    public void createAssignment(long childId, long parentId) throws DatabaseException {
        String cypher = "MATCH (a {id:" + childId + "}), (b {id:" + parentId + "}) " +
                "CREATE (a)-[:assigned_to]->(b)";
        execute(cypher);
    }

    @Override
    public void deleteAssignment(long childId, long parentId) throws DatabaseException {
        String cypher = "match (a{id:" + childId + "})-[r:assigned_to]->(b{id:" + parentId + "}) delete r";
        execute(cypher);
    }

    @Override
    public void createAssociation(long uaId, long targetId, HashSet<String> operations, boolean inherit) throws DatabaseException {
        String ops = setToCypherArray(operations);
        String cypher = "MATCH (ua:UA{id:" + uaId + "}), (oa:OA {id:" + targetId + "}) " +
                "CREATE (ua)-[:association{label:'ar', inherit:'" + inherit + "', operations:" + ops + "}]->(oa)";
        execute(cypher);
    }

    @Override
    public void updateAssociation(long uaId, long targetId, boolean inherit, HashSet<String> ops) throws DatabaseException {
        String strOps = setToCypherArray(ops);
        String cypher = "MATCH (ua:UA {id:" + uaId + "})-[r:association]->(oa:OA{id:" + targetId + "}) " +
                "SET r.operations=" + strOps;
        execute(cypher);
    }

    @Override
    public void deleteAssociation(long uaId, long targetId) throws DatabaseException {
        String cypher = "match (a{id:" + uaId + "})-[r:association]->(b{id:" + targetId + "}) delete r";
        execute(cypher);
    }

    @Override
    public void createProhibition(String prohibitionName, HashSet<String> operations, boolean intersection, ProhibitionResource[] resources, ProhibitionSubject subject) throws DatabaseException {
        String cypher = "create (:" + PROHIBITION_LABEL + "{" +
                "name: '" + prohibitionName + "', " +
                "operations: " + setToCypherArray(operations) +
                ", intersection: " + intersection +
                "})";
        execute(cypher);

        for(ProhibitionResource pr : resources){
            addResourceToProhibition(prohibitionName, pr.getResourceId(), pr.isComplement());
        }

        setProhibitionSubject(prohibitionName, subject.getSubjectId(), subject.getSubjectType());
    }

    @Override
    public void deleteProhibition(String prohibitionName) throws DatabaseException {
        String cypher = "match(p:" + PROHIBITION_LABEL +") detach delete p";
        execute(cypher);
    }

    @Override
    public void addResourceToProhibition(String prohibitionName, long resourceId, boolean complement) throws DatabaseException {
        String cypher = "match(p:" + PROHIBITION_LABEL + "{name:'" + prohibitionName + "'}), (n{id:" + resourceId +"}) create (p)-[:" + PROHIBITION_LABEL +"{complement: " + complement + "}]->(n)";
        execute(cypher);
    }

    @Override
    public void deleteProhibitionResource(String prohibitionName, long resourceId) throws DatabaseException {
        String cypher = "match(n{id:" + resourceId + "})<-[r:" + PROHIBITION_LABEL +"]-(p:" + PROHIBITION_LABEL +"{name:'" + prohibitionName + "'}) delete r";
        execute(cypher);
    }

    @Override
    public void setProhibitionIntersection(String prohibitionName, boolean intersection) throws DatabaseException {
        String cypher = "match(d:" + PROHIBITION_LABEL +"{name:'" + prohibitionName + "'}) set d.intersection = " + intersection;
        execute(cypher);
    }

    @Override
    public void setProhibitionSubject(String prohibitionName, long subjectId, ProhibitionSubjectType subjectType) throws DatabaseException {
        String cypher;
        if(subjectType.equals(ProhibitionSubjectType.P)) {
            cypher = "match(p:" + PROHIBITION_LABEL + "{name:'" + prohibitionName + "'}) create (p)<-[:" + PROHIBITION_LABEL + "]-(:PP{subjectId:" + subjectId + ", subjectType:'" + subjectType + "'})";
        } else {
            cypher = "match(p:" + PROHIBITION_LABEL + "{name:'" + prohibitionName + "'}), (n{id:" + subjectId + ", type:'" + subjectType + "'}) create (p)<-[:" + PROHIBITION_LABEL + "]-(n)";
        }
        execute(cypher);
    }

    @Override
    public void setProhibitionOperations(String prohibitionName, HashSet<String> operations) throws DatabaseException {
        String opStr = setToCypherArray(operations);
        String cypher = "match(p:" + PROHIBITION_LABEL +"{name:'" + prohibitionName + "'}) set p.operations = " + opStr;
        execute(cypher);
    }

    @Override
    public void reset() throws DatabaseException {
        String cypher = "match(n) detach delete n";
        execute(cypher);
    }
}
