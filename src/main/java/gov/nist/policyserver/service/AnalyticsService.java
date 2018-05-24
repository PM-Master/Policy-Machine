package gov.nist.policyserver.service;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Constants.ALL_OPERATIONS;
import static gov.nist.policyserver.common.Constants.ANY_OPERATIONS;

public class AnalyticsService extends Service{
    public AnalyticsService
            () throws ConfigurationException {
        super();
    }

    public List<PmAccessEntry> getUsersPermissionsOn(long targetId)
            throws NodeNotFoundException, ConfigurationException {
        //check that the target node exists
        Node target = graph.getNode(targetId);
        if(target == null){
            throw new NodeNotFoundException(targetId);
        }

        return access.getUsersWithAccessOn(target);
    }

    public PmAccessEntry getUserPermissionsOn(long targetId, long userId)
            throws NodeNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException, ConfigurationException {
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
        HashSet<String> prohibitedOps = getProhibitedOps(target.getId(), user.getId(), ProhibitionSubjectType.U.toString());

        //remove prohibited ops
        userAccess.getOperations().removeAll(prohibitedOps);

        return userAccess;
    }

    public List<PmAccessEntry> getAccessibleChildren(long targetId, long userId) throws NodeNotFoundException, NoUserParameterException, ConfigurationException {
        //check that a user id is present
        if(userId == 0){
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

    public List<PmAccessEntry> getAccessibleNodes(long userId) throws NodeNotFoundException, NoUserParameterException, ConfigurationException {
        //check that the user id is present
        if(userId == 0){
            throw new NoUserParameterException();
        }

        //check the cache for the current user
        /*if(getDao().permissionsCache.get(userId) != null) {
            return getDao().permissionsCache.get(userId);
        }*/

        //check that the user exists
        Node user = graph.getNode(userId);
        if(user == null){
            throw new NodeNotFoundException(userId);
        }

        //get the accessible nodes and add it to the cache

        return access.getAccessibleNodes(user);
    }

    public List<PmAccessEntry> getAccessibleNodes(Node user) throws ConfigurationException {
        //get the accessible nodes and add it to the cache

        return access.getAccessibleNodes(user);
    }

    public HashSet<String> getProhibitedOps(long targetId, long subjectId, String subjectType) throws NodeNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException, ConfigurationException {
        if(subjectId == 0){
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

    public void checkPermissions(Node user, long process, long targetId, String reqPerm) throws MissingPermissionException, NoSubjectParameterException, NodeNotFoundException, InvalidProhibitionSubjectTypeException, ConfigurationException {
        if(user == null && process == 0) {
            throw new MissingPermissionException("No User or Process found in checking permissions");
        }

        if(user != null) {
            PmAccessEntry perms = getUserPermissionsOn(targetId, user.getId());
            HashSet<String> operations = perms.getOperations();

            if (!perms.getOperations().contains(reqPerm)
                    && !perms.getOperations().contains(ALL_OPERATIONS)
                    && !(reqPerm.equals(ANY_OPERATIONS) && !operations.isEmpty())) {
                throw new MissingPermissionException("User " + user.getName() + " does not have the correct permissions on " + targetId + ": " + reqPerm);
            }
        }

        if(process != 0) {
            HashSet<String> prohibitedOps = getProhibitedOps(targetId, process, ProhibitionSubjectType.P.toString());
            if(prohibitedOps.contains(reqPerm)) {
                throw new MissingPermissionException("Process " + process + " does not have the correct permissions on " + targetId + ": " + reqPerm);
            }
        }
    }
}
