package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import scala.reflect.internal.pickling.Translations;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static gov.nist.policyserver.dao.DAO.getDao;

public class SessionService extends Service{
    private NodeService nodeService;
    private AccessService accessService;

    public SessionService() throws ConfigurationException {
        nodeService = new NodeService();
        accessService = new AccessService();
    }

    public String createSession(String username, String password) throws InvalidNodeTypeException, InvalidPropertyException, NullNameException, NodeNameExistsException, NodeNameExistsInNamespaceException, ConfigurationException, NullTypeException, DatabaseException, NodeNotFoundException, NodeIdExistsException {
        //authenticate
        HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null, null);
        if(nodes.isEmpty()){
            throw new NodeNotFoundException(username);
        }

        Node userNode = nodes.iterator().next();

        //create session id
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        //create session node
        Property[] properties = new Property[]{
                new Property(Constants.SESSION_USER_ID_PROPERTY, String.valueOf(userNode.getId()))
        };
        nodeService.createNode(0, sessionId, NodeType.S.toString(), "Session for " + username, properties);


        return sessionId;
    }

    public List<PmAccessEntry> getSessionAccess(String sessionId) throws InvalidNodeTypeException, InvalidPropertyException, SessionDoesNotExistException, PropertyNotFoundException, NodeNotFoundException, NoUserParameterException {
        HashSet<Node> nodes = nodeService.getNodes(null, sessionId, NodeType.S.toString(), null, null);
        if(nodes.isEmpty()){
            throw new SessionDoesNotExistException(sessionId);
        }

        Node sessionNode = nodes.iterator().next();
        Property userIdProp = sessionNode.getProperty(Constants.SESSION_USER_ID_PROPERTY);

        return accessService.getAccessibleNodes(Long.parseLong(userIdProp.getValue()));
    }
}
