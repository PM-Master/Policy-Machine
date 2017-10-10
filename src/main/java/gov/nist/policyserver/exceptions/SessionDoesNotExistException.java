package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class SessionDoesNotExistException extends PmException{
    public SessionDoesNotExistException(String sessionId){
        super(Constants.ERR_SESSION_DOES_NOT_EXIST, String.format("Session with id %s does not exist", sessionId));
    }
}
