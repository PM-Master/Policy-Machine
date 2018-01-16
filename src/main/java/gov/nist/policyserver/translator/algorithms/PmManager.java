package gov.nist.policyserver.translator.algorithms;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.service.AccessService;
import gov.nist.policyserver.service.NodeService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PmManager {
    public static final String GET_ENTITY_ID = "getEntityId";
    public static final String GET_INTERSECTION = "getIntersection";
    public static final String GET_ACCESS_CHILDREN = "getAccessibleChildren";
    public static final String GET_ENTITY_NAME = "getNameOfEntityWithIdAndType";
    public static final String GET_PERMITTED_OPS = "getUserPermsOn";

    public static final String PATH_DELIM = "/";
    public static final String NAME_DELIM = "+";

    public static final String FILE_WRITE = "File write";
    public static final String FILE_READ = "File read";

    private BufferedReader in;
    private PrintWriter out;

    private Node        pmUser;
    private NodeService nodeService;
    private AccessService accessService;

    public PmManager() throws IOException {

    }

    public PmManager(String username, NodeService nodeService, AccessService accessService) throws NodeNotFoundException {
        this.nodeService = nodeService;
        this.accessService = accessService;
        this.pmUser = getPmUser(username);
    }

    private Node getPmUser(String username) throws NodeNotFoundException {
        try {
            HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null, null);
            if(!nodes.isEmpty()) {
                return nodes.iterator().next();
            }else {
                throw new NodeNotFoundException(username);
            }
        }
        catch (InvalidNodeTypeException | InvalidPropertyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Node getPmUser() {
        return pmUser;
    }

    public long getEntityId(String namespace, String name) throws InvalidNodeTypeException, NameInNamespaceNotFoundException, InvalidPropertyException {
        return nodeService.getNodeInNamespace(namespace, name).getId();
    }

    public List<Node> getAccessibleChildren(long id, String perm) throws NodeNotFoundException, NoUserParameterException {
        List<PmAccessEntry> accessibleChildren = accessService.getAccessibleChildren(id, pmUser.getId());
        List<Node> nodes = new ArrayList<>();
        for(PmAccessEntry entry : accessibleChildren) {
            if(entry.getOperations().contains(perm)) {
                nodes.add(entry.getTarget());
            }
        }

        return nodes;
    }

    public Node getIntersection(long columnPmId, long rowPmId) throws NodeNotFoundException, InvalidNodeTypeException {
        HashSet<Node> columnChildren = nodeService.getChildrenOfType(columnPmId, NodeType.O.toString());
        HashSet<Node> rowChildren = nodeService.getChildrenOfType(rowPmId, NodeType.O.toString());
        columnChildren.retainAll(rowChildren);
        if(!columnChildren.isEmpty()) {
            return columnChildren.iterator().next();
        }else{
            return null;
        }
    }
}
