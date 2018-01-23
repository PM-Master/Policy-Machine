package gov.nist.policyserver.evr.model.script.rule.event;

import gov.nist.policyserver.model.graph.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class PcSpec {
    private List<Node> pcs;
    private boolean isEach;

    public PcSpec() {
        pcs = new ArrayList<>();
        isEach = true;
    }

    public List<Node> getPcs() {
        return pcs;
    }

    public void setPcs(List<Node> pcs) {
        this.pcs = pcs;
    }

    public boolean isEach() {
        return isEach;
    }

    public void setEach(boolean each) {
        isEach = each;
    }
}
