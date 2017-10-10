package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class AssignmentExistsException extends PmException {
    private static final String ASSIGNMENT_EXISTS_ERRMSG = "There is already an assignment between %d (start) and %d (end)";

    public AssignmentExistsException(String message) {
        super(Constants.ERR_ASSIGNMENT_EXISTS, message);
    }

    public AssignmentExistsException(Integer start, Integer end) {
        super(Constants.ERR_ASSIGNMENT_EXISTS, String.format(
                ASSIGNMENT_EXISTS_ERRMSG, start, end));
    }
}
