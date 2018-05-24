package gov.nist.policyserver.service;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static gov.nist.policyserver.common.Constants.DESCRIPTION_PROPERTY;
import static gov.nist.policyserver.common.Constants.PASSWORD_PROPERTY;

public class SessionService extends Service{
    private NodeService nodeService;
    private AssignmentService assignmentService;
    private AnalyticsService permissionsService;

    public SessionService() throws ConfigurationException {
        nodeService = new NodeService();
        assignmentService = new AssignmentService();
        permissionsService = new AnalyticsService();
    }

    public String createSession(String username, String password) throws InvalidNodeTypeException, InvalidPropertyException, NodeNotFoundException, PropertyNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException, PMAccessDeniedException, NullNameException, NodeNameExistsException, NodeNameExistsInNamespaceException, ConfigurationException, NullTypeException, NodeIdExistsException, DatabaseException, AssignmentExistsException {
        //authenticate
        HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null, null);
        if(nodes.isEmpty()){
            throw new NodeNotFoundException(username);
        }

        Node userNode = nodes.iterator().next();

        //check password
        //get stored password
        Property property = userNode.getProperty(PASSWORD_PROPERTY);
        String storedPass = property.getValue();

        if(!checkPasswordHash(storedPass, password)) {
            throw new PMAccessDeniedException("Username and password does not match.");
        }

        //create session id
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        //create session node
        Property[] properties = new Property[] {
                new Property(DESCRIPTION_PROPERTY, "Session for " + username)
        };
        Node sessionNode = nodeService.createNode(0, sessionId, NodeType.S.toString(), properties);

        //assign user node to session node
        assignmentService.createAssignment(userNode.getId(), sessionNode.getId());

        return sessionId;
    }

    public String createSession(String username) throws InvalidNodeTypeException, InvalidPropertyException, NullNameException, NodeNameExistsException, NodeNameExistsInNamespaceException, ConfigurationException, NullTypeException, DatabaseException, NodeNotFoundException, NodeIdExistsException, AssignmentExistsException {
        //get the user node
        HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null, null);
        if(nodes.size() != 1) {
            throw new NodeNotFoundException(username);
        }
        Node userNode = nodes.iterator().next();

        //create session id
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        //create session node
        Property[] properties = new Property[] {
                new Property(DESCRIPTION_PROPERTY, "Session for " + username)
        };
        Node sessionNode = nodeService.createNode(0, sessionId, NodeType.S.toString(), properties);

        //assign user node to session node
        assignmentService.createAssignment(userNode.getId(), sessionNode.getId());

        return sessionId;
    }

    public void deleteSession(String sessionId) throws InvalidNodeTypeException, InvalidPropertyException, DatabaseException, ConfigurationException, SessionDoesNotExistException, NodeNotFoundException {
        HashSet<Node> nodes = nodeService.getNodes(null, sessionId, NodeType.S.toString(), null, null);
        if(nodes.isEmpty()) {
            throw new SessionDoesNotExistException(sessionId);
        }

        Node sessionNode = nodes.iterator().next();

        nodeService.deleteNode(sessionNode.getId());
    }

    public List<PmAccessEntry> getSessionAccess(String sessionId) throws InvalidNodeTypeException, InvalidPropertyException, SessionDoesNotExistException, PropertyNotFoundException, NodeNotFoundException, NoUserParameterException, ConfigurationException {
        HashSet<Node> nodes = nodeService.getNodes(null, sessionId, NodeType.S.toString(), null, null);
        if(nodes.isEmpty()){
            throw new SessionDoesNotExistException(sessionId);
        }

        Node sessionNode = nodes.iterator().next();
        Property userIdProp = sessionNode.getProperty(Constants.SESSION_USER_ID_PROPERTY);

        return permissionsService.getAccessibleNodes(Long.parseLong(userIdProp.getValue()));
    }
}
