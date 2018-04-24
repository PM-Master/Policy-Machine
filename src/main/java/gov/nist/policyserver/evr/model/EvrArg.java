package gov.nist.policyserver.evr.model;

public class EvrArg {
    String value;
    EvrFunction function;
    EvrEntity entity;

    public EvrArg() {

    }

    public EvrArg(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public EvrArg(EvrFunction function) {
        this.function = function;
    }

    public EvrFunction getFunction() {
        return this.function;
    }

    public boolean isFunction() {
        return function != null;
    }

    public EvrArg(EvrEntity entity) {
        this.entity = entity;
    }

    public EvrEntity getEntity() {
        return entity;
    }

    public boolean isEntity() {
        return entity != null;
    }

    public boolean isValue() {
        return value != null;
    }
}
