package gov.nist.policyserver.evr;

public class EvrRequest {
    private String scriptName;
    private String source;

    public EvrRequest() {

    }

    public EvrRequest(String scriptName, String source) {
        this.scriptName = scriptName;
        this.source = source;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
