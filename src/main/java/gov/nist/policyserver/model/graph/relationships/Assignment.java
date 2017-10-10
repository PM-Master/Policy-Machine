package gov.nist.policyserver.model.graph.relationships;

import gov.nist.policyserver.model.graph.nodes.Node;
import org.jgrapht.graph.DefaultEdge;

import java.io.Serializable;

public class Assignment<V> extends DefaultEdge  implements Serializable {
    Node start;
    Node end;

    public Assignment(Node start, Node end){
        this.start = start;
        this.end = end;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public boolean equals(Object o){
        if(o instanceof Assignment){
            Assignment e = (Assignment)o;
            return start == e.getStart() && end == e.getEnd();
        }
        return false;
    }
}
