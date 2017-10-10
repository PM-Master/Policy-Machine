package gov.nist.policyserver.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"stackTrace", "cause", "localizedMessage", "suppressed"})
public class PmException extends Exception {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = 200;
    public static final int CLIENT_ERROR = 400;
    public static final int SERVER_ERROR = 500;

    private int code;

    public PmException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getErrorCode() {
        return code;
    }
}
