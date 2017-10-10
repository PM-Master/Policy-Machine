package gov.nist.policyserver.exceptions;

public class StartUpException extends PmException {

    private static final long serialVersionUID = 1L;
    private static final String STARTUP_ERRMSG = "The server encountered an error on startup";
    private static final int CODE = 1023;

    public StartUpException() {
        super(CODE, STARTUP_ERRMSG);
    }

    public StartUpException(String m) {
        super(CODE, m);
    }
}

