package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class ProhibitionDoesNotExistException extends PmException {
    public ProhibitionDoesNotExistException(String prohibitionName) {
        super(Constants.ERR_PROHIBITION_DOES_NOT_EXIST, String.format("Prohibition with name '%s' does not exist", prohibitionName));
    }
}

