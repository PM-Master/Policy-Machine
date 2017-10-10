package gov.nist.policyserver.requests;

import java.util.HashSet;

public class AssociationRequest {
    long            uaId;
    long            targetId;
    HashSet<String> ops;
    boolean         inherit;

    public long getUaId() {
        return uaId;
    }

    public void setUaId(long uaId) {
        this.uaId = uaId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public HashSet<String> getOps() {
        return ops;
    }

    public void setOps(HashSet<String> ops) {
        this.ops = ops;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }
}
