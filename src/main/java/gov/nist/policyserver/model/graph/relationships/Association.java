package gov.nist.policyserver.model.graph.relationships;

import gov.nist.policyserver.model.graph.nodes.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public class Association<V> extends Assignment  implements Serializable {
    private HashSet<String> operations;
    private boolean inherit;
    public Association(Node start, Node end, HashSet<String> ops, boolean inherit) {
        super(start, end);
        this.operations = ops;
        this.inherit = inherit;
    }

    public HashSet<String> getOps(){
        return this.operations;
    }

    public void addOperation(String op) {
        operations.add(op);
    }

    public void addOperations(List<String> ops) {
        this.operations.addAll(ops);
    }

    public void removeOperations(HashSet<String> operations) {
        this.operations.removeAll(operations);
    }

    public void setOperations(HashSet<String> ops){
        this.operations = ops;
    }

    public boolean isInherit(){
        return inherit;
    }

    public void setInherit(boolean inherit){
        this.inherit = inherit;
    }

    public void setAttributes(Node startNode, Node endNode) {
        this.start = startNode;
        this.end = endNode;
    }
}