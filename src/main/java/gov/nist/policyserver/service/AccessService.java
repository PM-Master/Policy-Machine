package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.relationships.Association;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.dao.DAO.getDao;

public class AccessService {

    private PmGraph  graph;
    private PmAccess access;

    public AccessService() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();
    }

    public void grantAccess(long uaId, long targetId, HashSet<String> ops, boolean inherit)
            throws NodeNotFoundException, DatabaseException, ConfigurationException {
        //TODO what if there is already an association?

        //check that the target and user attribute nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        Node ua = graph.getNode(uaId);
        if(ua == null){
            throw new NodeNotFoundException(uaId);
        }

        //create association in database
        getDao().createAssociation(uaId, targetId, ops, inherit);

        //create association in nodes
        graph.createAssociation(uaId, targetId, ops, inherit);
    }

    public List<PmAccessEntry> getUsersWithAccessOn(long targetId)
            throws NodeNotFoundException {
        //check that the target node exists
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }

        return access.getUsersWithAccessOn(target);
    }

    public PmAccessEntry getUserAccessOn(long targetId, long userId)
            throws PmException {
        //check that the target and user nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        Node user = graph.getNode(userId);
        if(user == null){
            throw new NodeNotFoundException(userId);
        }

        //get permitted ops for the subject on the target
        PmAccessEntry userAccess = access.getUserAccessOn(user, target);

        //get prohibited ops for the subject on the target
        HashSet<String> prohibitedOps = null;
        try {
            prohibitedOps = getProhibitedOps(target.getId(), user.getId(), ProhibitionSubjectType.U.toString());
        }
        catch (NoSubjectParameterException | InvalidProhibitionSubjectTypeException e) {
            throw new PmException(PmException.SERVER_ERROR, "The server encountered an error when calculating the prohibited ops for " + userId + " on " + targetId);
        }

        //remove prohibited ops
        userAccess.getOperations().removeAll(prohibitedOps);

        return userAccess;
    }


    public void updateAccess(long targetId, long uaId, boolean add, HashSet<String> newOps, boolean inherit)
            throws NodeNotFoundException, AssociationDoesNotExistException, DatabaseException, ConfigurationException {
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

        //check if adding or removing operations
        HashSet<String> ops = assoc.getOps();
        if (add) {
            ops.addAll(newOps);
        }
        else {
            ops.removeAll(newOps);
        }

        //update association in database
        getDao().updateAssociation(uaId, targetId, inherit, ops);

        //update association in nodes
        graph.updateAssociation(uaId, targetId, ops, inherit);
    }

    public void removeAccess(long targetId, long uaId) throws NoUserParameterException, NodeNotFoundException, AssociationDoesNotExistException, DatabaseException, ConfigurationException {
        //check the user attribute id is present
        if(uaId <= 0){
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

    public List<PmAccessEntry> getAccessibleChildren(long targetId, long userId) throws NodeNotFoundException, NoUserParameterException {
        //check that a user id is present
        if(userId <= 0){
            throw new NoUserParameterException();
        }

        //check that the user and target nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        Node user = graph.getNode(userId);
        if(user == null){
            throw new NodeNotFoundException(userId);
        }

        return access.getAccessibleChildrenOf(target, user);
    }

    public List<PmAccessEntry> getAccessibleNodes(long userId) throws NodeNotFoundException, NoUserParameterException {
        //check that the user id is present
        if(userId <= 0){
            throw new NoUserParameterException();
        }

        //check that the user exists
        Node user = graph.getNode(userId);
        if(user == null){
            throw new NodeNotFoundException(userId);
        }

        return access.getAccessibleNodes(user);
    }

    public HashSet<String> getProhibitedOps(long targetId, long subjectId, String subjectType) throws NodeNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException {
        if(subjectId <= 0){
            throw new NoSubjectParameterException();
        }

        //check if the subject type is valid
        ProhibitionSubjectType type = ProhibitionSubjectType.toProhibitionSubjectType(subjectType);

        //check that the user and target nodes exist
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        if(type.equals(ProhibitionSubjectType.U)) {
            Node user = graph.getNode(subjectId);
            if (user == null) {
                throw new NodeNotFoundException(subjectId);
            }
        }

        return access.getProhibitedOps(targetId, subjectId);
    }

    private Association getAssociation(long uaId, long targetId) throws AssociationDoesNotExistException {
        Association association = graph.getAssociation(uaId, targetId);
        if(association == null){
            throw new AssociationDoesNotExistException(uaId, targetId);
        }

        return association;
    }

    public List<Association> getTargetAssociations(long targetId) throws NodeNotFoundException {
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }
        return graph.getTargetAssociations(targetId);
    }

    public List<Association> getUattrAssociations(long uaId) throws NodeNotFoundException {
        Node target = graph.getNode(uaId);
        if(target == null){
            throw new NodeNotFoundException(uaId);
        }
        return graph.getUattrAssociations(uaId);
    }

    public List<Association> getAssociations() {
        return graph.getAssociations();
    }

    public void updateAssociation(long targetId, long uaId, HashSet<String> ops) throws NodeNotFoundException, AssociationDoesNotExistException, ConfigurationException, DatabaseException {
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
        getDao().updateAssociation(uaId, targetId, Constants.INHERIT_DEFAULT, ops);

        //update association in nodes
        graph.updateAssociation(uaId, targetId, ops, Constants.INHERIT_DEFAULT);
    }


}
