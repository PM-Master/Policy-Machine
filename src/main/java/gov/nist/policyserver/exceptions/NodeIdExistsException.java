package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.model.graph.nodes.Node;

public class NodeIdExistsException extends PmException {
    public NodeIdExistsException(long id, Node node) {
        super(Constants.ERR_NODE_ID_EXISTS, "A node already exists with ID " + id + ": name=" + node.getName() + ", type=" + node.getType());
    }
}
