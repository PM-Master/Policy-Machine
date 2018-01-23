package gov.nist.policyserver.evr.model.script.rule.event;

import java.util.List;

public class OpSpec {
    List<String> ops;

    public OpSpec(List<String> ops) {
        this.ops = ops;
    }

    public List<String> getOps() {
        return ops;
    }

    public void setOps(List<String> ops) {
        this.ops = ops;
    }
}
