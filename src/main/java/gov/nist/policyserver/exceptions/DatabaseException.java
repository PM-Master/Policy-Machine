package gov.nist.policyserver.exceptions;

public class DatabaseException extends PmException {
    public DatabaseException(int code, String msg) {
        super(code, msg);
    }
}
