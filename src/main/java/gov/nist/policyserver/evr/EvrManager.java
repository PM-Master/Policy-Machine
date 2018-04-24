package gov.nist.policyserver.evr;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.evr.exceptions.InvalidEntityException;
import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import gov.nist.policyserver.evr.model.*;
import gov.nist.policyserver.evr.model.script.EvrScript;
import gov.nist.policyserver.evr.model.script.rule.event.EvrOpSpec;
import gov.nist.policyserver.evr.model.script.rule.event.EvrPcSpec;
import gov.nist.policyserver.evr.model.script.rule.event.EvrSubject;
import gov.nist.policyserver.evr.model.script.rule.event.EvrTarget;
import gov.nist.policyserver.evr.model.script.rule.event.time.EvrEvent;
import gov.nist.policyserver.evr.model.script.rule.response.*;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;
import gov.nist.policyserver.service.AccessService;
import gov.nist.policyserver.service.AssignmentService;
import gov.nist.policyserver.service.NodeService;
import gov.nist.policyserver.service.ProhibitionService;
import gov.nist.policyserver.translator.algorithms.DbManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static gov.nist.policyserver.common.Constants.NAMESPACE_PROPERTY;

public class EvrManager {
    private static final String INSERT_EVENT      = "insert";
    private static final String DELETE_EVENT      = "delete";
    private static final String UPDATE_EVENT      = "update";
    private static final String SELECT_EVENT      = "select";
    private static final String OBJ_CREATE_EVENT  = "Object create";
    private static final String OBJ_DESTROY_EVENT = "Object destroy";
    private static final String OBJ_READ_EVENT    = "Object read";
    private static final String OBJ_WRITE_EVENT   = "Object write";
    private static final String USER_CREATE_EVENT = "User create";

    private EvrParser            parser;
    private List<EvrScript>      scripts;
    //private EvrEventProcessor    eventProcessor;
    private EvrResponseProcessor responseProcessor;
    private NodeService          nodeService;
    private AssignmentService    assignmentService;
    private AccessService        accessService;
    private ProhibitionService   prohibitionService;
    private DbManager            dbManager;
    private List<String>         activeSqls;
    private List<EvrEntity> curSubjects;

    public EvrManager() throws ConfigurationException {
        parser = new EvrParser();
        scripts = new ArrayList<>();
        nodeService = new NodeService();
        assignmentService = new AssignmentService();
        prohibitionService = new ProhibitionService();
        accessService = new AccessService();
        activeSqls = new ArrayList<>();
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    public void parse(String xml) throws InvalidEvrException, InvalidPropertyException, IOException, SAXException, ParserConfigurationException {
        parser.loadScript(xml);
        EvrScript script = parser.parseScript();
        script.setEnabled(true);
        scripts.add(script);
    }

    public void addScript(EvrScript script) {
        scripts.add(script);
    }

    public List<EvrScript> getScripts() {
        return scripts;
    }

    public void addActiveSql(String id) {
        activeSqls.add(id);
    }

    public boolean isActiveSql(String id) {
        return activeSqls.contains(id);
    }

    public void removeActiveSql(String id) {
        activeSqls.remove(id);
    }

    public void processUpdate(Node user, String process, Update update) throws InvalidPropertyException, InvalidNodeTypeException, InvalidEvrException, NodeNotFoundException, InvalidEntityException, SQLException, ConfigurationException, DatabaseException, AssignmentExistsException, ProhibitionResourceExistsException, ProhibitionNameExistsException, ProhibitionDoesNotExistException, InvalidProhibitionSubjectTypeException {
        //if user is null, its a process
        EvrSubject evrSubject = new EvrSubject();
        if(process != null) {
            evrSubject.addEntity(new EvrEntity(new EvrProcess(process)));
        } else {
            evrSubject.addEntity(new EvrEntity(user));
        }

        //process updating table
        HashSet<Node> node = nodeService.getNodes(update.getTable().getName(),
                update.getTable().getName(), NodeType.OA.toString(), null);
        if(node.size() != 1) {
            throw new InvalidEvrException("Invalid event target Node in process Update.");
        }

        processEvent(evrSubject, new EvrPcSpec(), UPDATE_EVENT, node.iterator().next());

        //process updating columns
        List<Column> columns = update.getColumns();
        for(Column column : columns) {
           node = nodeService.getNodes(update.getTable().getName(),
                    column.getColumnName(), NodeType.OA.toString(), null);
            if(node.size() != 1) {
                throw new InvalidEvrException("Invalid event target Node in process Update.");
            }

            processEvent(evrSubject, new EvrPcSpec(), UPDATE_EVENT, node.iterator().next());
        }

        //process updating rows
        List<String> rows = dbManager.getRows();
        for(String row : rows) {
            node = nodeService.getNodes(update.getTable().getName(),
                    row, NodeType.OA.toString(), null);
            if(node.size() != 1) {
                throw new InvalidEvrException("Invalid event target Node in process Update.");
            }

            processEvent(evrSubject, new EvrPcSpec(), UPDATE_EVENT, node.iterator().next());
        }
    }

    public void processSelect(Node user, String process) throws InvalidPropertyException, InvalidNodeTypeException, InvalidEvrException, NodeNotFoundException, InvalidEntityException, SQLException, ConfigurationException, DatabaseException, AssignmentExistsException, ProhibitionResourceExistsException, ProhibitionNameExistsException, ProhibitionDoesNotExistException, InvalidProhibitionSubjectTypeException {
        //if user is null, its a process
        EvrSubject evrSubject = new EvrSubject();
        if(process != null) {
            evrSubject.addEntity(new EvrEntity(new EvrProcess(process)));
        } else {
            evrSubject.addEntity(new EvrEntity(user));
        }

        HashSet<Column> columns = dbManager.getColumns();
        HashMap<String, List<String>> tableRows = dbManager.getTableRows();

        //process select tables
        for(String tableName : tableRows.keySet()) {
            HashSet<Node> node = nodeService.getNodes(tableName,
                    tableName, NodeType.OA.toString(), null);
            if (node.size() != 1) {
                throw new InvalidEvrException("Invalid event target Node in process Select.");
            }

            processEvent(evrSubject, new EvrPcSpec(), SELECT_EVENT, node.iterator().next());
        }

        //process select columns
        for(Column column : columns) {
            HashSet<Node> node = nodeService.getNodes(column.getTable().getName(),
                    column.getColumnName(), NodeType.OA.toString(), null);
            if(node.size() != 1) {
                throw new InvalidEvrException("Invalid event target Node in process Select.");
            }

            processEvent(evrSubject, new EvrPcSpec(), SELECT_EVENT, node.iterator().next());
        }

        //process select rows
        for(String tableName : tableRows.keySet()) {
            List<String> rows = tableRows.get(tableName);
            for(String row : rows) {
                HashSet<Node> nodes = nodeService.getNodes(tableName,
                        row, NodeType.OA.toString(), null);
                if(nodes.size() != 1) {
                    throw new InvalidEvrException("Invalid event target Node in process Select.");
                }

                Node node = nodes.iterator().next();

                processEvent(evrSubject, new EvrPcSpec(), SELECT_EVENT, node);

                HashSet<Node> children = nodeService.getChildrenOfType(node.getId(), null);
                for(Node n : children) {
                    processEvent(evrSubject, new EvrPcSpec(), SELECT_EVENT, n);
                }
            }
        }
    }

    public void processEvent(EvrSubject procSubject, EvrPcSpec procPc, String procEvent, Node procTarget) throws InvalidNodeTypeException, InvalidEntityException, NodeNotFoundException, InvalidPropertyException, InvalidEvrException, SQLException, DatabaseException, ConfigurationException, AssignmentExistsException, ProhibitionResourceExistsException, ProhibitionNameExistsException, ProhibitionDoesNotExistException, InvalidProhibitionSubjectTypeException {
        //get all rules with the same event
        List<EvrRule> rules = getRules(procEvent);
        for(EvrRule rule : rules) {
            EvrEvent evrEvent = rule.getEvent();
            if(eventMatches(evrEvent, procSubject, procPc, procTarget)) {
                EvrResponse response = rule.getResponse();
                EvrCondition condition = response.getCondition();
                if(checkCondition(condition)) {
                    this.curSubjects = procSubject.getEntities();

                    List<EvrAction> actions = response.getActions();
                    doActions(actions);
                }
            }
        }

    }

    private boolean checkCondition(EvrCondition condition) throws InvalidEntityException, InvalidNodeTypeException, InvalidEvrException, InvalidPropertyException, SQLException {
        if(condition == null) {
            return true;
        }

        EvrEntity entity = condition.getEntity();

        //if the entity is a function, eval function set entity = result
        if(entity.isFunction()) {
            entity = evalFunction(entity.getFunction());
        }

        //if its a node and exists than return true
        if(entity.isNode()) {
            HashSet<Node> nodes = nodeService.getNodes(null, entity.getName(), entity.getType(), entity.getProperties());
            return !nodes.isEmpty();
        }

        //if its a value and is 'true' return true
        if(entity.isValue()) {
            return entity.getName().equalsIgnoreCase("true");
        }

        return false;
    }

    private boolean eventMatches(EvrEvent evrEvent, EvrSubject procSubject, EvrPcSpec procPc, Node procTarget) throws InvalidNodeTypeException, InvalidEntityException, NodeNotFoundException, InvalidPropertyException, InvalidEvrException, SQLException {
        //check subject
        EvrSubject evrSubject = evrEvent.getSubject();
        EvrPcSpec evrPc = evrEvent.getPcSpec();
        EvrTarget evrTarget = evrEvent.getTarget();

        return subjectMatches(procSubject, evrSubject) &&
                pcMatches(procPc, evrPc) &&
                targetMatches(procTarget, evrTarget);
    }

    private boolean subjectMatches(EvrSubject procSubject, EvrSubject evrSubject) throws InvalidNodeTypeException, InvalidPropertyException, InvalidEntityException, NodeNotFoundException, InvalidEvrException, SQLException {
        //if the event being checked is any than it matches
        if(evrSubject.isAny()) {
            return true;
        }

        List<EvrEntity> procEntities = procSubject.getEntities();
        List<EvrEntity> eventEntities = evrSubject.getEntities();
        for(EvrEntity procEntity : procEntities) {
            for(EvrEntity evrEntity : eventEntities) {
                //check process
                if(evrEntity.isProcess()) {
                    if(!procEntity.isProcess()) {
                        continue;
                    } else if (!checkProcess(procEntity, evrEntity)) {
                        return false;
                    }
                }

                //check node
                if(evrEntity.isNode()) {
                    if(!procEntity.isNode()) {
                        continue;
                    } else if(!checkNode(procEntity, evrEntity)) {
                        return false;
                    }
                }

                //if the entity is a function
                if(evrEntity.isFunction()) {
                    EvrEntity funcEntity = evalFunction(procEntity.getFunction());
                    //check process
                    if(funcEntity.isProcess()) {
                        if(!procEntity.isProcess()) {
                            continue;
                        } else if (!checkProcess(procEntity, evrEntity)) {
                            return false;
                        }
                    }

                    //check node
                    if(funcEntity.isNode()) {
                        if(!procEntity.isNode()) {
                            continue;
                        } else if(!checkNode(procEntity, evrEntity)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean pcMatches(EvrPcSpec procPc, EvrPcSpec evrPc) {
        if(evrPc == null) {
            return true;
        }

        if(evrPc.isAny()) {
            return true;
        }

        //TODO
        return true;
    }

    private boolean targetMatches(Node procTarget, EvrTarget evrTarget) throws InvalidNodeTypeException, InvalidPropertyException, InvalidEvrException, NodeNotFoundException {
        EvrEntity evrTargetEntity = evrTarget.getEntity();
        List<EvrEntity> evrTargetContainers = evrTarget.getContainers();

        Node evrTargetNode = null;

        if(!evrTargetEntity.isAny()) {
            // check that the nodes match
            HashSet<Node> nodes =
                    nodeService.getNodes(null, evrTargetEntity.getName(), evrTargetEntity.getType(), evrTargetEntity.getProperties());
            if(nodes.size() != 1) {
                throw new InvalidEvrException("Target entity (" + evrTargetEntity.getName() + ") can only be one node");
            }

            evrTargetNode = nodes.iterator().next();
            if(!procTarget.equals(evrTargetNode)) {
                return false;
            }
        }

        if(!evrTarget.isAnyContainer()) {
            //make sure the entity is in at least one container
            for(EvrEntity evrEntity : evrTargetContainers) {
                HashSet<Node> nodes = nodeService.getNodes(null, evrEntity.getName(), evrEntity.getType(), evrEntity.getProperties());
                for(Node node : nodes) {
                    if(evrTargetNode != null) {
                        HashSet<Node> ascendants = assignmentService.getAscendants(evrTargetNode.getId());
                        ascendants.add(evrTargetNode);
                        if(ascendants.contains(node)) {
                            return true;
                        }
                    } else {
                        //any object, check the processed target is in the container
                        HashSet<Node> ascendants = assignmentService.getAscendants(node.getId());
                        ascendants.add(node);
                        if(ascendants.contains(procTarget)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean checkProcess(EvrEntity procEntity, EvrEntity evrEntity) {
        return procEntity.isProcess() &&
                evrEntity.isProcess() &&
                procEntity.getProcess().equals(evrEntity.getProcess());
    }

    /**
     * Evaluate a function
     * @param function
     * @return
     */
    private EvrEntity evalFunction(EvrFunction function) throws InvalidEvrException, InvalidNodeTypeException, InvalidPropertyException, SQLException {
        List<EvrEntity> args = evalArgs(function.getArgs());

        switch (function.getFunctionName()) {
            case "getNodeWithProperty":
                return getNodeWithProperty(args);
            case "getSqlValue":
                return getSqlValue(args);
            case "checkSqlValue":
                //EvrEntity name will be the value "true" or "false"
                return checkSqlValue(args);
            case "current_process":
                return currentProcess();
            default:
                throw new InvalidEvrException("Exception with name " + function.getFunctionName() + " does not exist");
        }
    }

    private EvrEntity currentProcess() {
        for (EvrEntity evrEntity : curSubjects) {
            if(evrEntity.isProcess()) {
                return evrEntity;
            }
        }

        return null;
    }

    private EvrEntity getSqlValue(List<EvrEntity> args) throws InvalidEvrException, SQLException {
        //args should be db, table, column
        if(args.size() != 3) {
            throw new InvalidEvrException("Invalid number of parameters for function 'getSqlValue'. Expected 3, got " + args.size());
        }

        //check arg 1
        EvrEntity db = args.get(0);
        if(!db.isValue()) {
            throw new InvalidEvrException("First parameter for function 'getSqlValue' should be a value");
        }

        //check arg 2
        EvrEntity table = args.get(1);
        if(!table.isValue()) {
            throw new InvalidEvrException("Second parameter for function 'getSqlValue' should be a value");
        }

        //check arg 3
        EvrEntity column = args.get(2);
        if(!column.isValue()) {
            throw new InvalidEvrException("Third parameter for function 'getSqlValue' should be a value");
        }

        EvrEntity evrEntity = new EvrEntity();

        String sql = "select " + column.getName() + " from " + db.getName() + "." + table.getName() +
                " where " + getKeyString(table.getName()) + " " + getInString();
        Connection connection = dbManager.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()) {
            evrEntity = new EvrEntity(String.valueOf(resultSet.getObject(1)), null, null, false);
        }

        return evrEntity;
    }

    /**
     * number of args = 2
     * 1. property name
     * 2. property value
     * @param args
     * @return
     * @throws InvalidEvrException
     */
    private EvrEntity getNodeWithProperty(List<EvrEntity> args) throws InvalidEvrException, InvalidNodeTypeException, InvalidPropertyException {
        if(args.size() != 2) {
            throw new InvalidEvrException("Invalid number of parameters for function 'getNodeWithProperty'. Expected 2, got " + args.size());
        }

        //check arg 1
        EvrEntity propName = args.get(0);
        if(!propName.isValue()) {
            throw new InvalidEvrException("First parameter for function 'getNodeWithProperty' should be a value");
        }

        //check arg 2
        EvrEntity propValue = args.get(1);
        if(!propValue.isValue()) {
            throw new InvalidEvrException("Second parameter for function 'getNodeWithProperty' should be a value");
        }

        HashSet<Node> nodes =
                nodeService.getNodes(null, null, null, propName.getName(), propValue.getName());

        List<EvrEntity> evrEntities = new ArrayList<>();
        for(Node node : nodes) {
            evrEntities.add(new EvrEntity(node));
        }

        return new EvrEntity(evrEntities);
    }

    /**
     * Take in a list of EvrArgs and return a list with no functions
     * @param args
     * @return
     */
    private List<EvrEntity> evalArgs(List<EvrArg> args) throws InvalidEvrException, InvalidNodeTypeException, InvalidPropertyException, SQLException {
        List<EvrEntity> retArgs = new ArrayList<>();
        for(EvrArg arg : args) {
            if(arg.isFunction()) {
                retArgs.add(evalFunction(arg.getFunction()));
            } else if(arg.isEntity()){
                retArgs.add(arg.getEntity());
            } else if(arg.isValue()) {
                retArgs.add(new EvrEntity(arg.getValue(), null, null, false));
            }
        }

        return retArgs;
    }

    /**
     * This method assumes both are nodes, check for functions or processes are done elsewhere
     * @param procEntity
     * @param evrEntity
     * @return
     */
    private boolean checkNode(EvrEntity procEntity, EvrEntity evrEntity) throws InvalidNodeTypeException, InvalidPropertyException, InvalidEntityException, NodeNotFoundException {
        HashSet<Node> nodes =
                nodeService.getNodes(null, evrEntity.getName(), evrEntity.getType(), evrEntity.getProperties());
        if(nodes.size() != 1) {
            throw new InvalidEntityException("The node (" + evrEntity.getName() + ", " + evrEntity.getType() + ") specified in the EVR script does not exist.");
        }

        Node checkNode = nodes.iterator().next();

        //check that the nodes are equal
        if(procEntity.getNode().equals(checkNode)) {
            return true;
        } else {
            //is the checked node an ascendant to the processed node
            nodes = assignmentService.getAscendants(procEntity.getNode().getId());
            if(nodes.contains(checkNode)) {
                return true;
            }
        }

        //if it gets to this point, the nodes do not match
        return false;
    }


    private EvrEntity checkSqlValue(List<EvrEntity> args) throws SQLException {
        String table = args.get(0).getName();
        String column = args.get(1).getName();
        String expected = args.get(2).getName();

        String keyStr = getKeyString(table);
        String inStr = getInString();

        String sql = "select " + column + " from " + table + " where " + keyStr + " " + inStr;

        Connection connection = dbManager.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()) {
            if(resultSet.getBoolean(1) != Boolean.parseBoolean(expected)) {
                return new EvrEntity("false", null, null, false);
            }
        }

        return new EvrEntity("true", null, null, false);
    }

    private String getKeyString(String table) throws SQLException {
        List<String> keys = getKeys(table);
        String concatKey = "concat(";
        for(int i = 0; i < keys.size(); i++){
            if(i == 0) {
                concatKey += keys.get(i);
            }else{
                concatKey += ",'+'," + keys.get(i);
            }
        }
        concatKey += ")";

        return concatKey;
    }

    private String getInString() {
        List<String> rows = dbManager.getRows();
        String in = "in (";
        for(int i = 0; i < rows.size(); i++) {
            if(i == 0) {
                in += "'" + rows.get(i) + "'";
            } else {
                in += ", '" + rows.get(i) + "'";
            }
        }
        in += ")";

        return in;
    }

    protected List<String> getKeys(String tableName) throws SQLException {
        PreparedStatement ps2 = dbManager.getConnection().prepareStatement("SELECT k.COLUMN_NAME " +
                "FROM information_schema.table_constraints t " +
                "LEFT JOIN information_schema.key_column_usage k " +
                "USING(constraint_name,table_schema,table_name) " +
                "WHERE t.constraint_type='PRIMARY KEY' " +
                "    AND t.table_schema=DATABASE() " +
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

    public List<EvrRule> getRules(String event) {
        List<EvrRule> retRules = new ArrayList<>();
        for(EvrScript script : scripts) {
            if(script.isEnabled()) {
                List<EvrRule> rules = script.getRules();
                for (EvrRule rule : rules) {
                    EvrEvent ruleEvent = rule.getEvent();

                    //check the event matches
                    if (ruleEvent.getOpSpec().getOps().contains(event)) {
                        retRules.add(rule);
                    }
                }
            }
        }

        return retRules;
    }

    private void doActions(List<EvrAction> actions) throws InvalidEntityException, InvalidNodeTypeException, SQLException, InvalidEvrException, InvalidPropertyException, NodeNotFoundException, DatabaseException, ConfigurationException, AssignmentExistsException, ProhibitionResourceExistsException, ProhibitionNameExistsException, ProhibitionDoesNotExistException, InvalidProhibitionSubjectTypeException {
        for(EvrAction action : actions) {
            if(action instanceof EvrGrantAction) {
                EvrGrantAction grantAction = (EvrGrantAction) action;
                doGrant(grantAction);
            } else if(action instanceof EvrAssignAction) {
                EvrAssignAction assignAction = (EvrAssignAction) action;
                doAssign(assignAction);
            } else if(action instanceof EvrDenyAction) {
                EvrDenyAction denyAction = (EvrDenyAction) action;
                doDeny(denyAction);
            }
        }
    }

    private void doDeny(EvrDenyAction denyAction) throws DatabaseException, ProhibitionDoesNotExistException, NodeNotFoundException, ConfigurationException, ProhibitionResourceExistsException, InvalidProhibitionSubjectTypeException, ProhibitionNameExistsException, InvalidEntityException, InvalidNodeTypeException, SQLException, InvalidEvrException, InvalidPropertyException {
        EvrSubject subject = denyAction.getSubject();
        EvrOpSpec opSpec = denyAction.getOpSpec();
        EvrTarget target = denyAction.getTarget();

        List<String> ops = opSpec.getOps();
        String[] opsArr = new String[ops.size()];
        opsArr = ops.toArray(opsArr);

        ProhibitionRes[] resources = new ProhibitionRes[target.getContainers().size()];
        List<EvrEntity> containers = target.getContainers();
        for(int i = 0; i < containers.size(); i++) {
            EvrEntity evrEntity = containers.get(i);
            if(evrEntity.isFunction()) {
                evrEntity = evalFunction(evrEntity.getFunction());
            }

            HashSet<Node> nodes = nodeService.getNodes(null, evrEntity.getName(), evrEntity.getType(), evrEntity.getProperties());
            if(nodes.size() != 1) {
                throw new NodeNotFoundException("Error finding container node when creating a prohibition");
            }
            Node node = nodes.iterator().next();
            resources[i] = new ProhibitionRes(node.getId(), evrEntity.isCompliment());
        }

        List<EvrEntity> entities = subject.getEntities();
        for(EvrEntity evrEntity : entities) {
            //check if function
            if(evrEntity.isFunction()) {
                evrEntity = evalFunction(evrEntity.getFunction());
            }

            //get node if node
            ProhibitionSubject proSubject = null;
            if(evrEntity.isNode()) {
                HashSet<Node> nodes = nodeService.getNodes(null, evrEntity.getName(), evrEntity.getType(), evrEntity.getProperties());
                if (nodes.size() != 1) {
                    throw new NodeNotFoundException("Error finding subject node when creating a prohibition");
                }
                Node node = nodes.iterator().next();
                proSubject = new ProhibitionSubject(node.getId(), ProhibitionSubjectType.toProhibitionSubjectType(node.getType().toString()));
            } else {
                //its a process
                EvrProcess process = evrEntity.getProcess();
                if(process.isFunction()) {
                    evrEntity = evalFunction(process.getFunction());
                }

                proSubject = new ProhibitionSubject(Long.valueOf(evrEntity.getProcess().getProcessId()), ProhibitionSubjectType.P);
            }


            prohibitionService.createProhibition(UUID.randomUUID().toString(),
                    opsArr, target.isIntersection(), resources, proSubject);
        }
    }

    private void doAssign(EvrAssignAction assignAction) throws InvalidEntityException, InvalidNodeTypeException, SQLException, InvalidEvrException, InvalidPropertyException {
        EvrEntity child = assignAction.getChild();
        EvrEntity parent = assignAction.getParent();

        if(child.isFunction()) {
            child = evalFunction(child.getFunction());
        }

        if(parent.isFunction()) {
            parent = evalFunction(parent.getFunction());
        }

        HashSet<Node> childNodes = getNodes(child);
        HashSet<Node> parentnodes = getNodes(parent);

        for(Node childNode : childNodes) {
            for(Node parentNode : parentnodes) {
                System.out.println("Assigning " + childNode.getName() + " to " + parentNode.getName());
                //assignmentService.createAssignment(childNode.getId(), parentNode.getId());
            }
        }
    }

    private HashSet<Node> getNodes(EvrEntity evrEntity) throws InvalidEntityException, InvalidNodeTypeException, InvalidPropertyException {
        HashSet<Node> nodes = new HashSet<>();
        if(evrEntity.isList()) {
            List<EvrEntity> entityList = evrEntity.getEntityList();
            for(EvrEntity entity : entityList) {
                if(entity.isNode()) {
                    nodes.add(entity.getNode());
                } else if(entity.isEvrNode()) {
                    HashSet<Node> entityNodes = nodeService.getNodes(null, entity.getName(), entity.getType(), entity.getProperties());
                    nodes.addAll(entityNodes);
                }
            }
        } else if(evrEntity.isEvrNode()) {
            HashSet<Node> entityNodes = nodeService.getNodes(null, evrEntity.getName(), evrEntity.getType(), evrEntity.getProperties());
            nodes.addAll(entityNodes);
        } else if(evrEntity.isNode()) {
            nodes.add(evrEntity.getNode());
        }

        return nodes;
    }


    /**
     * grant each subject the ops on the target
     * @param grantAction
     */
    private void doGrant(EvrGrantAction grantAction) throws InvalidEntityException, InvalidNodeTypeException, SQLException, InvalidEvrException, InvalidPropertyException, DatabaseException, NodeNotFoundException, ConfigurationException {
        EvrSubject subject = grantAction.getSubject();
        List<EvrEntity> entities = subject.getEntities();

        EvrOpSpec opSpec = grantAction.getOpSpec();
        List<String> ops = opSpec.getOps();

        EvrTarget target = grantAction.getTarget();
        List<EvrEntity> containers = target.getContainers();

        for(EvrEntity subjectEntity : entities) {
            if(subjectEntity.isFunction()) {
                subjectEntity = evalFunction(subjectEntity.getFunction());
            }

            HashSet<Node> subjectNodes = getNodes(subjectEntity);

            //loop through the nodes that are included in the subject entity
            for(Node subjectNode : subjectNodes) {
                if(!subjectNode.getType().equals(NodeType.UA)) {//can only grant access for ua
                    continue;
                }

                for(EvrEntity targetContainer : containers) {
                    if(targetContainer.isFunction()) {
                        targetContainer = evalFunction(targetContainer.getFunction());
                    }

                    HashSet<Node> targetNodes = getNodes(targetContainer);

                    for(Node targetNode : targetNodes) {
                        System.out.println("Granting " + subjectNode.getName() + " " + ops + " on " + targetNode.getName());
                        //accessService.grantAccess(subjectNode.getId(), targetNode.getId(), new HashSet<>(ops), true);
                    }
                }
            }
        }
    }
}
