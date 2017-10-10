package gov.nist.policyserver.model.access;

import gov.nist.policyserver.model.graph.nodes.Node;

import java.util.HashSet;

public class PmAccessEntry {
    Node            target;
    HashSet<String> operations;

    public PmAccessEntry(){

    }

    public PmAccessEntry(Node target, HashSet<String> operations) {
        this.target = target;
        this.operations = operations;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public HashSet<String> getOperations() {
        return operations;
    }

    public void setOperations(HashSet<String> operations) {
        this.operations = operations;
    }
}
