package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class InvalidPropertyException extends PmException {
    public InvalidPropertyException(String key, String value){
        super(Constants.ERR_INVALID_PROPERTY, "The property '" + key + "=" + value + "' is invalid");
    }
}