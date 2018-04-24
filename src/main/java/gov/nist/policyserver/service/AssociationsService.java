package gov.nist.policyserver.service;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.relationships.Association;

import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.dao.DAO.getDao;

public class AssociationsService extends Service{
    public AssociationsService() throws ConfigurationException {
        super();
    }
    public void createAssociation(long uaId, long targetId, HashSet<String> ops, boolean inherit) throws NodeNotFoundException, ConfigurationException, DatabaseException, AssociationExistsException {
        //TODO

        //check that the target and user attribute nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }

        Node ua = graph.getNode(uaId);
        if(ua == null){
            throw new NodeNotFoundException(uaId);
        }

        Association association = graph.getAssociation(uaId, targetId);
        if(association != null) {
            throw new AssociationExistsException(uaId, targetId);
        }

        //create association in database
        getDao().createAssociation(uaId, targetId, ops, inherit);

        //create association in nodes
        graph.createAssociation(uaId, targetId, ops, inherit);
    }

    public void updateAssociation(long targetId, long uaId, HashSet<String> ops, boolean inherit) throws NodeNotFoundException, AssociationDoesNotExistException, ConfigurationException, DatabaseException {
        //TODO

        //check that the target and user attribute nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        Node ua = graph.getNode(uaId);
        if(ua == null){
            throw new NodeNotFoundException(uaId);
        }

        //check ua -> oa exists
        Association assoc = getAssociation(uaId, targetId);
        if (assoc == null) {
            throw new AssociationDoesNotExistException(uaId, targetId);
        }

        //update association in database
        getDao().updateAssociation(uaId, targetId, inherit, ops);

        //update association in nodes
        graph.updateAssociation(uaId, targetId, ops, inherit);
    }

    private Association getAssociation(long uaId, long targetId) throws AssociationDoesNotExistException {
        Association association = graph.getAssociation(uaId, targetId);
        if(association == null){
            throw new AssociationDoesNotExistException(uaId, targetId);
        }

        return association;
    }

    public void deleteAssociation(long targetId, long uaId) throws NoUserParameterException, NodeNotFoundException, AssociationDoesNotExistException, ConfigurationException, DatabaseException {
        //TODO

        //check the user attribute id is present
        if(uaId == 0){
            throw new NoUserParameterException();
        }

        //check that the nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        Node ua = graph.getNode(uaId);
        if(ua == null){
            throw new NodeNotFoundException(uaId);
        }

        //check ua -> oa exists
        Association assoc = getAssociation(uaId, targetId);
        if (assoc == null) {
            throw new AssociationDoesNotExistException(uaId, targetId);
        }

        //delete the association in database
        getDao().deleteAssociation(uaId, targetId);

        //delete the association in nodes
        graph.deleteAssociation(uaId, targetId);
    }

    public List<Association> getTargetAssociations(long targetId) throws NodeNotFoundException {
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        return graph.getTargetAssociations(targetId);
    }

    public List<Association> getSubjectAssociations(long subjectId) throws NodeNotFoundException {
        Node target = graph.getNode(subjectId);
        if(target == null){
            throw new NodeNotFoundException(subjectId);
        }
        return graph.getUattrAssociations(subjectId);
    }

    public List<Association> getAssociations() {
        return graph.getAssociations();
    }
}
