package gov.nist.policyserver.evr.model.script.rule.response;

import gov.nist.policyserver.evr.model.script.rule.event.EvrOpSpec;
import gov.nist.policyserver.evr.model.script.rule.event.EvrSubject;
import gov.nist.policyserver.evr.model.script.rule.event.EvrTarget;

public class EvrGrantAction extends EvrAction {
    private EvrSubject subject;
    private EvrOpSpec  opSpec;
    private EvrTarget  target;

    public EvrSubject getSubject() {
        return subject;
    }

    public void setSubject(EvrSubject subject) {
        this.subject = subject;
    }

    public EvrOpSpec getOpSpec() {
        return opSpec;
    }

    public void setOpSpec(EvrOpSpec opSpec) {
        this.opSpec = opSpec;
    }

    public EvrTarget getTarget() {
        return target;
    }

    public void setTarget(EvrTarget target) {
        this.target = target;
    }
}
