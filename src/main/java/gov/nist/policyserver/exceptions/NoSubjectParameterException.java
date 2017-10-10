package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class NoSubjectParameterException extends PmException {
    public NoSubjectParameterException() {
        super(Constants.ERR_NO_SUBJECT_PARAMETER, "No user or process was specified in the parameters, but one is required.");
    }
}
