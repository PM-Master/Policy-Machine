package gov.nist.policyserver.evr.model.script.rule.event;

public class Event {
    UserSpec userSpec;
    PcSpec pcSpec;
    OpSpec opSpec;
    ObjSpec objSpec;

    public Event() {

    }

    public UserSpec getUserSpec() {
        return userSpec;
    }

    public void setUserSpec(UserSpec userSpec) {
        this.userSpec = userSpec;
    }

    public PcSpec getPcSpec() {
        return pcSpec;
    }

    public void setPcSpec(PcSpec pcSpec) {
        this.pcSpec = pcSpec;
    }

    public OpSpec getOpSpec() {
        return opSpec;
    }

    public void setOpSpec(OpSpec opSpec) {
        this.opSpec = opSpec;
    }

    public ObjSpec getObjSpec() {
        return objSpec;
    }

    public void setObjSpec(ObjSpec objSpec) {
        this.objSpec = objSpec;
    }
}
