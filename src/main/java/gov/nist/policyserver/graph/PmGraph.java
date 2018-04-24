package gov.nist.policyserver.graph;

import gov.nist.policyserver.exceptions.PropertyNotFoundException;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.model.graph.relationships.Assignment;
import gov.nist.policyserver.model.graph.relationships.Association;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DirectedMultigraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PmGraph implements Serializable{
    DirectedGraph<Node, Assignment> graph;
    public PmGraph(){
        graph = new DirectedMultigraph<>(Assignment.class);
    }

    public synchronized void addNode(Node n){
        graph.addVertex(n);
    }

    public synchronized void updateNode(long nodeId, String name){
        Node n = getNode(nodeId);
        if(name != null && !name.isEmpty()) {
            n.setName(name);
        }
    }

    public synchronized void deleteNode(long nodeId){
        Node n = getNode(nodeId);
        graph.removeVertex(n);
    }

    public synchronized void deleteNode(Node node){
        graph.removeVertex(node);
    }

    public synchronized void addEdge(Node child, Node parent, Assignment edge){
        graph.addEdge(child, parent, edge);
    }

    public Node getNode(long nodeId){
        HashSet<Node> nodes = getNodes();
        for(Node n : nodes){
            if(n.getId() == nodeId){
                return n;
            }
        }
        return null;
    }

    public synchronized HashSet<Node> getChildren(long nodeId){
        Node n = getNode(nodeId);

        HashSet<Node> children = new HashSet<>();
        Set<Assignment> assignments = graph.incomingEdgesOf(n);
        for(Assignment edge : assignments){
            children.add(edge.getChild());
        }
        return children;
    }

    public synchronized HashSet<Node> getParents(long nodeId){
        Node n = getNode(nodeId);

        HashSet<Node> parents = new HashSet<>();
        Set<Assignment> assignments = graph.outgoingEdgesOf(n);
        for(Assignment edge : assignments){
            parents.add(edge.getParent());
        }
        return parents;
    }

    public synchronized HashSet<Node> getChildren(Node n){
        HashSet<Node> children = new HashSet<>();
        Set<Assignment> assignments = graph.incomingEdgesOf(n);
        for(Assignment edge : assignments){
            children.add(edge.getChild());
        }
        return children;
    }

    public synchronized HashSet<Node> getParents(Node n){
        HashSet<Node> parents = new HashSet<>();
        Set<Assignment> assignments = graph.outgoingEdgesOf(n);
        for(Assignment edge : assignments){
            parents.add(edge.getParent());
        }
        return parents;
    }

    public synchronized HashSet<Assignment> outgoingEdgesOf(Node n){
        return new HashSet<>(graph.outgoingEdgesOf(n));
    }

    public synchronized HashSet<Assignment> incomingEdgesOf(Node n){
        return new HashSet<>(graph.incomingEdgesOf(n));
    }

    public HashSet<Node> getNodesOfType(NodeType type){
        HashSet<Node> nodes = getNodes();
        nodes.removeIf(node -> !node.getType().equals(type));
        return nodes;
    }

    /**
     * When looking at a nodes, ascendants are the nodes above a given node.  i.e. the children, grandchildren, etc.
     * @param node the node to get the ascendants for
     * @return a HashSet of Nodes
     */
    public synchronized HashSet<Node> getAscesndants(Node node){
        HashSet<Node> ascendants = new HashSet<>();
        HashSet<Node> children = getChildren(node);
        if(children.isEmpty()){
            return ascendants;
        }

        ascendants.addAll(children);

        for(Node child : children){
            ascendants.addAll(getAscesndants(child));
        }

        return ascendants;
    }

    public synchronized HashSet<Node> getAscesndants(long nodeId){
        Node node = getNode(nodeId);
        HashSet<Node> ascendants = new HashSet<>();
        HashSet<Node> children = getChildren(node);
        if(children.isEmpty()){
            return ascendants;
        }

        ascendants.addAll(children);

        for(Node child : children){
            ascendants.addAll(getAscesndants(child));
        }

        return ascendants;
    }

    public synchronized HashSet<Node> getNodes(){
        HashSet<Node> nodes = new HashSet<>(graph.vertexSet());
        nodes.removeIf(node -> node.getType() == null);
        return nodes;
    }

    public synchronized void deleteNodeProperty(long nodeId, String key){
        Node node = getNode(nodeId);
        node.deleteProperty(key);
    }

    public synchronized void updateNodeProperty(long nodeId, String key, String value) throws PropertyNotFoundException {
        Node node = getNode(nodeId);
        node.updateProperty(key, value);
    }

    public synchronized void addNodeProperty(long nodeId, Property property){
        Node node = getNode(nodeId);
        node.addProperty(property);
    }

    public synchronized void createAssignment(long childId, long parentId){
        Node child = getNode(childId);
        Node parent = getNode(parentId);
        graph.addEdge(child, parent, new Assignment<>(child, parent));
    }

    public synchronized void createAssignment(Node child, Node parent){
        graph.addEdge(child, parent, new Assignment<>(child, parent));
    }

    public synchronized void deleteAssignment(long childId, long parentId){
        Node child = getNode(childId);
        Node parent = getNode(parentId);
        graph.removeEdge(child, parent);
    }

    public HashSet<Assignment> getAssignments() {
        HashSet<Assignment> assignments = new HashSet<>();
        Set<Assignment> edges = graph.edgeSet();
        for(Assignment a : edges){
            if(!(a instanceof Association)){
                assignments.add(a);
            }
        }

        return assignments;
    }

    public synchronized void deleteAssignment(Node child, Node parent){
        graph.removeEdge(child, parent);
    }

    public synchronized void createAssociation(long uaId, long targetId, HashSet<String> operations, boolean inherit){
        Node ua = getNode(uaId);
        Node target = getNode(targetId);
        graph.addEdge(ua, target, new Association<>(ua, target, operations, inherit));
    }

    public synchronized void createAssociation(Node ua, Node target, HashSet<String> operations, boolean inherit){
        graph.addEdge(ua, target, new Association<>(ua, target, operations, inherit));
    }

    public synchronized void updateAssociation(long uaId, long targetId, HashSet<String> ops, boolean inherit){
        Set<Assignment> edges = graph.getAllEdges(getNode(uaId), getNode(targetId));
        for(Assignment edge : edges){
            if(edge instanceof Association){
                ((Association) edge).setOperations(ops);
                ((Association) edge).setInherit(inherit);
            }
        }
    }

    public synchronized void deleteAssociation(long uaId, long targetId){
        deleteAssignment(uaId, targetId);
    }

    public synchronized void deleteAssociation(Node ua, Node target){
        deleteAssignment(ua, target);
    }

    public synchronized Association getAssociation(long uaId, long targetId){
        return (Association) graph.getEdge(getNode(uaId), getNode(targetId));
    }

    public List<Association> getUattrAssociations(long uaId){
        List<Association> assocs = new ArrayList<>();
        Set<Assignment> assignments = graph.outgoingEdgesOf(getNode(uaId));
        for(Assignment edge : assignments){
            if(edge instanceof Association){
                Association assocEdge = (Association)edge;
                assocs.add(new Association(assocEdge.getChild(), assocEdge.getParent(), assocEdge.getOps(), assocEdge.isInherit()));
            }
        }
        return assocs;
    }

    public List<Association> getTargetAssociations(long targetId){
        List<Association> assocs = new ArrayList<>();
        Set<Assignment> assignments = graph.incomingEdgesOf(getNode(targetId));
        for(Assignment edge : assignments){
            if(edge instanceof Association){
                Association assocEdge = (Association)edge;
                assocs.add(new Association(assocEdge.getChild(), assocEdge.getParent(), assocEdge.getOps(), assocEdge.isInherit()));
            }
        }
        return assocs;
    }

    public synchronized boolean isAssigned(long childId, long parentId){
        Assignment edge = graph.getEdge(getNode(childId), getNode(parentId));
        return !(edge == null || edge instanceof Association);
    }

    public synchronized boolean isAssigned(Node child, Node parent){
        Assignment edge = graph.getEdge(child, parent);
        return !(edge == null || edge instanceof Association);
    }

    public List<Association> getAssociations() {
        List<Association> assocs = new ArrayList<>();
        Set<Assignment> edges = graph.edgeSet();
        for(Assignment assignment : edges){
            if(assignment instanceof Association){
                Association assocEdge = (Association)assignment;
                assocs.add(new Association(assocEdge.getChild(), assocEdge.getParent(), assocEdge.getOps(), assocEdge.isInherit()));
            }
        }
        return assocs;
    }
}
