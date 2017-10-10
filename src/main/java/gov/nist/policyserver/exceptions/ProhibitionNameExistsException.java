package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class ProhibitionNameExistsException extends PmException {
    public ProhibitionNameExistsException(String prohibitionName) {
        super(Constants.ERR_PROHIBITION_NAME_EXISTS, String.format("Prohibition with name '%s' already exists", prohibitionName));
    }
}
