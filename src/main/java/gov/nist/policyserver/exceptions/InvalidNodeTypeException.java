package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class InvalidNodeTypeException extends PmException {
    public InvalidNodeTypeException(String type){
        super(Constants.ERR_INVALID_NODETYPE, "Provided NodeType '" + type + "' is not one of (C, OA, UA, U, O, PC, D, OS)");
    }
    public InvalidNodeTypeException(int type){
        super(Constants.ERR_INVALID_NODETYPE, "Provided NodeType ID " + type + " is not between 1-7");
    }
}