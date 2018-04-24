package gov.nist.policyserver.evr.model.script.rule.event.time;

import gov.nist.policyserver.evr.model.script.rule.event.EvrOpSpec;
import gov.nist.policyserver.evr.model.script.rule.event.EvrPcSpec;
import gov.nist.policyserver.evr.model.script.rule.event.EvrSubject;
import gov.nist.policyserver.evr.model.script.rule.event.EvrTarget;

public class EvrEvent {
    EvrSubject subject;
    EvrPcSpec  pcSpec;
    EvrOpSpec  opSpec;
    EvrTarget  target;
    EvrTime    time;

    public EvrSubject getSubject() {
        return subject;
    }

    public void setSubject(EvrSubject subject) {
        this.subject = subject;
    }

    public EvrPcSpec getPcSpec() {
        return pcSpec;
    }

    public void setPcSpec(EvrPcSpec pcSpec) {
        this.pcSpec = pcSpec;
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

    public void setTime(EvrTime time) {
        this.time = time;
    }

    public EvrTime getTime() {
        return time;
    }

    public boolean isTime() {
        return time != null;
    }

    public boolean equals(Object o) {
        if(!(o instanceof EvrEvent)) {
            return false;
        }

        EvrEvent event = (EvrEvent) o;

        //if one is time and the other not time return false
        if(this.isTime() != event.isTime()) {
            return false;
        } else if(this.getTime().equals(event.getTime())) {
            return true;
        }

        //if it gets to this point, it is not time
        return this.getSubject().equals(event.getSubject()) &&
                this.getPcSpec().equals(event.getPcSpec()) &&
                this.getOpSpec().equals(event.getOpSpec()) &&
                this.getTarget().equals(event.getTarget());
    }
}
