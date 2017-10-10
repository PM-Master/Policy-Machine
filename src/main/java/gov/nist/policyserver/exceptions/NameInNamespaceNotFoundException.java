package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NameInNamespaceNotFoundException extends PmException {
    public NameInNamespaceNotFoundException(String namespace, String nodeName) {
        super(Constants.ERR_NAME_IN_NAMESPACE_NOT_FOUND, String.format("Node with name '%s' does not exist in namespace %s", nodeName, namespace));
    }
}
