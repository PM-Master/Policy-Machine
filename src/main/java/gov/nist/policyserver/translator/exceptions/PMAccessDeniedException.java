package gov.nist.policyserver.translator.exceptions;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.exceptions.PmException;

public class PMAccessDeniedException extends PmException {
    public PMAccessDeniedException(String column){
        super(Constants.ERR_ACCESS_DENIED, "The column \"" + column + "\" is inaccessible");
    }
}
