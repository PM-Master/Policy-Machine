package gov.nist.policyserver.evr.model.script.rule.response;

import gov.nist.policyserver.evr.model.EvrEntity;

public class EvrAssignAction extends EvrAction {
    private EvrEntity child;
    private EvrEntity parent;

    public EvrEntity getChild() {
        return child;
    }

    public void setChild(EvrEntity child) {
        this.child = child;
    }

    public EvrEntity getParent() {
        return parent;
    }

    public void setParent(EvrEntity parent) {
        this.parent = parent;
    }
}
