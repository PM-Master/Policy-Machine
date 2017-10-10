package gov.nist.policyserver.exceptions;

public class CustomException extends PmException {
    private static final long serialVersionUID = 1L;
    private static final int CODE = 118;

    public CustomException(String ErrorMsg) {
        super(CODE, String.format(ErrorMsg));
    }
}
