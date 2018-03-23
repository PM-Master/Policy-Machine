package gov.nist.policyserver.model.graph.relationships;

import gov.nist.policyserver.model.graph.nodes.Node;
import org.jgrapht.graph.DefaultEdge;

import java.io.Serializable;

public class Assignment<V> extends DefaultEdge  implements Serializable {
    Node child;
    Node parent;

    public Assignment(Node child, Node parent){
        this.child = child;
        this.parent = parent;
    }

    public Node getChild() {
        return child;
    }

    public Node getParent() {
        return parent;
    }

    public boolean equals(Object o){
        if(o instanceof Assignment){
            Assignment e = (Assignment)o;
            return child == e.getChild() && parent == e.getParent();
        }
        return false;
    }
}
