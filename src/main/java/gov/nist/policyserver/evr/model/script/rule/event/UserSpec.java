package gov.nist.policyserver.evr.model.script.rule.event;

import gov.nist.policyserver.model.graph.nodes.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserSpec {
    List<Node> nodes;

    public UserSpec() {
        nodes = new ArrayList<>();
    }

    public UserSpec addNodes(Collection<Node> nodes) {
        this.nodes.addAll(nodes);
        return this;
    }

    public List<Node> getNodes() {
        return this.nodes;
    }
}
