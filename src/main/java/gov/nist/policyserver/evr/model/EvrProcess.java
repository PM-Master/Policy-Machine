package gov.nist.policyserver.evr.model;

public class EvrProcess {
    private String processId;
    private EvrFunction evrFunction;

    public EvrProcess() {

    }

    public EvrProcess(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return this.processId;
    }

    public EvrProcess(EvrFunction evrFunction) {
        this.evrFunction = evrFunction;
    }

    public boolean isFunction() {
        return evrFunction != null;
    }

    public EvrFunction getFunction() {
        return evrFunction;
    }
}
