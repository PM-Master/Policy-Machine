package gov.nist.policyserver.evr.model;

import java.util.ArrayList;
import java.util.List;

public class EvrFunction {
    private String       functionName;
    private List<EvrArg> args;


    public EvrFunction(String functionName) {
        this.functionName = functionName;
        this.args = new ArrayList<>();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public List<EvrArg> getArgs() {
        return args;
    }

    public void setArgs(List<EvrArg> args) {
        this.args = args;
    }

    public void addArg(EvrArg evrArg) {
        this.args.add(evrArg);
    }
}
