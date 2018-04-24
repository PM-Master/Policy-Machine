package gov.nist.policyserver.service;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.HashSet;

import static gov.nist.policyserver.common.Constants.*;
import static gov.nist.policyserver.dao.DAO.getDao;

public class AssignmentService extends Service{
    private NodeService nodeService = new NodeService();

    public AssignmentService() throws ConfigurationException {
        super();
    }

    public boolean isAssigned(long childId, long parentId) throws NodeNotFoundException {
        //check if the nodes exist
        Node child = graph.getNode(childId);
        if(child == null){
            throw new NodeNotFoundException(childId);
        }
        Node parent = graph.getNode(parentId);
        if(parent == null){
            throw new NodeNotFoundException(parentId);
        }

        return graph.isAssigned(child, parent);
    }

    public void createAssignment(long childId, long parentId) throws NodeNotFoundException, AssignmentExistsException, ConfigurationException, DatabaseException {
        //check if the nodes exist
        Node child = graph.getNode(childId);
        if(child == null){
            throw new NodeNotFoundException(childId);
        }
        Node parent = graph.getNode(parentId);
        if(parent == null){
            throw new NodeNotFoundException(parentId);
        }
        if (isAssigned(childId, parentId) ) {
            throw new AssignmentExistsException("Assignment exists between node " + childId + " and " + parentId);
        }
        //create assignment in database
        getDao().createAssignment(childId, parentId);

        //create assignment in nodes
        graph.createAssignment(child, parent);
    }

    public void deleteAssignment(long childId, long parentId) throws NodeNotFoundException, AssignmentDoesNotExistException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException {
        //check if the nodes exist
        Node child = graph.getNode(childId);
        if(child == null){
            throw new NodeNotFoundException(childId);
        }
        Node parent = graph.getNode(parentId);
        if(parent == null){
            throw new NodeNotFoundException(childId);
        }

        //check if the assignment exists
        if(!isAssigned(childId, parentId)){
            throw new AssignmentDoesNotExistException(childId, parentId);
        }

        //delete assignment in database
        getDao().deleteAssignment(childId, parentId);

        //delete assignment in nodes
        graph.deleteAssignment(child, parent);
    }

    public HashSet<Node> getAscendants(long nodeId) throws NodeNotFoundException {
        Node node = graph.getNode(nodeId);
        if(node == null) {
            throw new NodeNotFoundException(nodeId);
        }

        return graph.getAscesndants(nodeId);
    }
}
