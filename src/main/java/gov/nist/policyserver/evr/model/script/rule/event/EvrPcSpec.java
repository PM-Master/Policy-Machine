package gov.nist.policyserver.evr.model.script.rule.event;

import gov.nist.policyserver.evr.model.EvrEntity;

import java.util.ArrayList;
import java.util.List;

public class EvrPcSpec {
    private List<EvrEntity> pcs;
    private boolean         or;

    public EvrPcSpec() {
        pcs = new ArrayList<>();
        or = true;
    }

    public List<EvrEntity> getPcs() {
        return pcs;
    }

    public void setPcs(List<EvrEntity> pcs) {
        this.pcs = pcs;
    }

    public boolean isOr() {
        return or;
    }

    public void setOr(boolean or) {
        this.or = or;
    }

    public void addEntity(EvrEntity evrEntity) {
        this.pcs.add(evrEntity);
    }

    public boolean isAny() {
        return pcs.isEmpty();
    }
}
