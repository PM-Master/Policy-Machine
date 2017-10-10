package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class ProhibitionResourceExistsException extends PmException {
    public ProhibitionResourceExistsException(String prohibtionName, long resourceId) {
        super(Constants.ERR_PROHIBITION_RESOURCE_EXISTS, String.format("Prohibition with name '%s' already has a resource with id %d", prohibtionName, resourceId));
    }
}
