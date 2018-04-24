package gov.nist.policyserver.translator.exceptions;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.PmException;

public class PMAccessDeniedException extends PmException {
    public PMAccessDeniedException(String node){
        super(Constants.ERR_ACCESS_DENIED, "The attribute \"" + node + "\" is inaccessible");
    }
}
