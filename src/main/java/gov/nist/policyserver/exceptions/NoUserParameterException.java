package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NoUserParameterException extends PmException {
    public NoUserParameterException() {
        super(Constants.ERR_NO_USER_PARAMETER, "No user or user attribute was specified in the parameters, but one is required.");
    }
}
