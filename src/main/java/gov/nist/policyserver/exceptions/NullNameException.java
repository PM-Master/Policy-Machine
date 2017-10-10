package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NullNameException extends PmException {
    public NullNameException() {
        super(Constants.ERR_NULL_NAME, "The server received a null name");
    }
}
