package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NullTypeException extends PmException {
    public NullTypeException() {
        super(Constants.ERR_NULL_TYPE, "The server received a null type");
    }
}
