package gov.nist.policyserver.dao;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.evr.EvrManager;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.DatabaseException;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Assignment;
import gov.nist.policyserver.model.prohibitions.ProhibitionResource;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.io.*;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public abstract class DAO {
    public static DAO         dao;
    private static ClassLoader classLoader;
    private static boolean     reinitializing;

    static String database;
    static String host;
    static int    port;
    static String username;
    static String password;
    static String schema;
    static int interval = 30;


    /**
     * @return The static instance of the DaoHelper
     */
    public static DAO getDao() throws ConfigurationException {
        if(dao == null){
            throw new ConfigurationException("There is no database connection. Visit /pm/config.jsp to connect to a database.");
        }
        return dao;
    }

    public static String getDatabase(){
        return database;
    }
    /**
     * This method is called from the StartupServlet, with the properties being read from pm.conf
     * @param
     * @throws DatabaseException
     */
    public static void init() throws DatabaseException, ConfigurationException {
        try {
            //deserialize
            FileInputStream fis = new FileInputStream("pm.conf");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Properties props = (Properties) ois.readObject();

            //get properties
            database = props.getProperty("database");
            host = props.getProperty("host");
            port = Integer.parseInt(props.getProperty("port"));
            schema = props.getProperty("schema");
            username = props.getProperty("username");
            password = props.getProperty("password");
            String inter = props.getProperty("interval");
            if(inter != null) {
                interval = Integer.parseInt(inter);
            }

            //want deserialization
            if(database.equalsIgnoreCase(Constants.NEO4J)){
                dao = new NeoDAO();
            }else{
                dao = new SqlDAO();
            }
            dao.buildScripts();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called from the configuration service to update connection info.
     * It then writes the properties to pm.config
     * @param props
     * @throws DatabaseException
     */
    public static void init(Properties props) throws DatabaseException, ConfigurationException {
        //get properties
        database = props.getProperty("database");
        host = props.getProperty("host");
        port = Integer.parseInt(props.getProperty("port"));
        schema = props.getProperty("schema");
        username = props.getProperty("username");
        password = props.getProperty("password");
        String inter = props.getProperty("interval");
        if(inter != null) {
            interval = Integer.parseInt(inter);
        }

        //serialize thr properties
        saveProperties(props);

        //re initialize dao instance, not deserializing
        reinitializing = true;
        if(database.equalsIgnoreCase(Constants.NEO4J)){
            dao = new NeoDAO();
        }else{
            dao = new SqlDAO();
        }
        dao.buildScripts();
    }

    public static synchronized void setInterval(int newInterval) throws ConfigurationException {
        if(newInterval > 0){
            interval = newInterval;
        }else{
            throw new ConfigurationException("Interval must be a value greater than 0");
        }

        if(database == null){
            throw new ConfigurationException("There is no database configuration for the Policy Machine.  Please set the connection configuration before setting the dump interval.");
        }

        Properties props = new Properties();
        props.put("database", database);
        props.put("host", host);
        props.put("port", String.valueOf(port));
        props.put("username", username);
        props.put("password", password);
        props.put("schema", schema == null ? "" : schema);
        props.put("interval", String.valueOf(interval));

        saveProperties(props);
    }

    private static void saveProperties(Properties props) {
        try {
            FileOutputStream fos = new FileOutputStream("pm.conf");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(props);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  PmGraph    graph;
    public  PmAccess   access;
    private EvrManager evrManager;

    public Connection conn;
    public DAO() throws DatabaseException {
        //connect to database
        connect();

        //if(reinitializing || !deserialize()) {
            System.out.println("Building nodes...");
            //build the nodes in memory
            buildGraph();

            //initialize the access object
            access = new PmAccess();

            //build the prohibitions list
            buildProhibitions();

            reinitializing = false;
            System.out.println("Finished!");
        //}

        Runnable r = () -> {
            while(true) {
                //System.out.println("Serializing... " + new Date());
                serialize();
                try {
                    Thread.sleep(interval * 1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r).start();
    }

    public EvrManager getEvrManager() {
        return evrManager;
    }

    public void buildScripts() throws ConfigurationException {
        evrManager = new EvrManager();
    }

    public PmGraph getGraph(){
        return graph;
    }

    public PmAccess getAccess(){
        return access;
    }

    /**
     * Abstract method to establish a connection to the database
     * @throws DatabaseException if the database is not running or if there are any other connection issues
     */
    public abstract void connect() throws DatabaseException;

    /**
     * Abstract method to build the in-memory nodes
     * @throws DatabaseException this method will access1 the database, so there may be connection issues
     */
    public abstract void buildGraph() throws DatabaseException;

    public abstract void buildProhibitions() throws DatabaseException;

    private void serialize(){
        try {
            FileOutputStream fos = new FileOutputStream("graph.conf");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);

            fos = new FileOutputStream("access.conf");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(access);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialize(String configName){
        try {
            FileOutputStream fos = new FileOutputStream("graph.conf");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);

            fos = new FileOutputStream("access.conf");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(access);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean deserialize(){
        try {
            FileInputStream fis = new FileInputStream("graph.conf");
            ObjectInputStream ois = new ObjectInputStream(fis);
            graph = (PmGraph) ois.readObject();

            fis = new FileInputStream("access.conf");
            ois = new ObjectInputStream(fis);
            access = (PmAccess) ois.readObject();

            System.out.println("deserialized...");
            return true;
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public abstract List<Assignment> getAssignments() throws DatabaseException;

    public abstract Node createNode(long id, String name, NodeType nt) throws DatabaseException;

    public abstract void updateNode(long nodeId, String name) throws DatabaseException;

    public abstract void deleteNode(long nodeId) throws DatabaseException;

    public abstract void addNodeProperty(long nodeId, Property property) throws DatabaseException;

    public abstract void deleteNodeProperty(long nodeId, String key) throws DatabaseException;

    public abstract void updateNodeProperty(long nodeId, String key, String value) throws DatabaseException;

    //assignment
    public abstract void createAssignment(long childId, long parentId) throws DatabaseException;

    public abstract void deleteAssignment(long childId, long parentId) throws DatabaseException;

    //association
    public abstract void createAssociation(long uaId, long targetId, HashSet<String> operations, boolean inherit) throws DatabaseException;

    public abstract void updateAssociation(long uaId, long targetId, boolean inherit, HashSet<String> ops) throws DatabaseException;

    public abstract void deleteAssociation(long uaId, long targetId) throws DatabaseException;

    //prohibitions
    public abstract void createProhibition(String prohibitionName, HashSet<String> operations, boolean intersection, ProhibitionResource[] resources, ProhibitionSubject subject) throws DatabaseException;

    public abstract void deleteProhibition(String prohibitionName) throws DatabaseException;

    public abstract void addResourceToProhibition(String prohibitionName, long resourceId, boolean compliment) throws DatabaseException;

    public abstract void deleteProhibitionResource(String prohibitionName, long resourceId) throws DatabaseException;

    public abstract void setProhibitionIntersection(String prohibitionName, boolean intersection) throws DatabaseException;

    public abstract void setProhibitionSubject(String prohibitionName, long subjectId, ProhibitionSubjectType subjectType) throws DatabaseException;

    public abstract void setProhibitionOperations(String prohibitionName, HashSet<String> ops) throws DatabaseException;

    public abstract void reset() throws DatabaseException;
}
