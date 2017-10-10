package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class AssociationDoesNotExistException extends PmException {
    public AssociationDoesNotExistException(long uaId, long targetId) {
        super(Constants.ERR_ASSOCIATION_DOES_NOT_EXIST, String.format("An association between %d and %d does not exist.", uaId, targetId));
    }
}
