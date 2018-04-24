package gov.nist.policyserver.evr.model.script.rule.event;

import java.util.List;

public class EvrOpSpec {
    private List<String> ops;

    public EvrOpSpec(List<String> ops) {
        this.ops = ops;
    }

    public List<String> getOps() {
        return ops;
    }

    public void setOps(List<String> ops) {
        this.ops = ops;
    }

    public boolean isAny() {
        return ops.isEmpty();
    }
}
