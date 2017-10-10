package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class AssignmentDoesNotExistException extends PmException {
    public AssignmentDoesNotExistException(long childId, long parentId) {
        super(Constants.ERR_ASSIGNMENT_DOES_NOT_EXIST, String.format("An assignment between %d and %d does not exist.", childId, parentId));
    }
}
