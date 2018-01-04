package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.exceptions.NodeNotFoundException;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;

import static gov.nist.policyserver.dao.DAO.getDao;

public class AssignmentService {

    private PmGraph  graph;
    private PmAccess access;

    public AssignmentService() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();
    }

    public AssignmentService(PmGraph graph, PmAccess access) {
        this.graph = graph;
        this.access = access;
    }

    public boolean isAssigned(long childId, long parentId) throws NodeNotFoundException {
        //check if the nodes exist
        Node child = graph.getNode(childId);
        if(child == null){
            throw new NodeNotFoundException(childId);
        }
        Node parent = graph.getNode(parentId);
        if(parent == null){
            throw new NodeNotFoundException(childId);
        }

        return graph.isAssigned(child, parent);
    }

    public void createAssignment(long childId, long parentId) throws NodeNotFoundException, ConfigurationException, DatabaseException, AssignmentExistsException {
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

    public void deleteAssignment(long childId, long parentId) throws NodeNotFoundException, AssignmentDoesNotExistException, ConfigurationException, DatabaseException {
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

}
