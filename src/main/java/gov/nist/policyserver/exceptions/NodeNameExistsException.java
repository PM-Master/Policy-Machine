package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NodeNameExistsException extends PmException {
    public NodeNameExistsException(String name) {
        super(Constants.ERR_NODE_NAME_EXISTS, String.format("A node with the name '%s' already exists", name));
    }
}
