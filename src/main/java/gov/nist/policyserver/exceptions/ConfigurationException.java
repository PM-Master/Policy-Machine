package gov.nist.policyserver.exceptions;

import gov.nist.policyserver.common.Constants;

public class ConfigurationException extends PmException {
    public ConfigurationException(String message) {
        super(Constants.ERR_CONFIGURATION, message);
    }
}
