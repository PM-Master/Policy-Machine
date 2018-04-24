package gov.nist.policyserver.access;

import gov.nist.policyserver.common.Constants;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.relationships.Assignment;
import gov.nist.policyserver.model.graph.relationships.Association;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;

import java.io.Serializable;
import java.util.*;

/**
 * Class that uses the algorithm to determine access1 to nodes
 */
public class PmAccess implements Serializable{
    private PmGraph           graph;
    private List<Prohibition> prohibitions;
    public PmAccess(PmGraph graph){
        this.graph = graph;
        prohibitions = new ArrayList<>();
    }

    /**
     * Get the access1 rights a user has on a node
     * @param user The user
     * @param target The node to get the access1 rights for
     * @return a HashSet of operations
     */
    public PmAccessEntry getUserAccessOn(Node user, Node target){
        PmAccessEntry entry = new PmAccessEntry();
        HashSet<String> ops = new HashSet<>();

        //get policy classes
        HashSet<Node> pcs = getPolicyClasses();

        //get border nodes.  Can be OA or UA.  Return empty set if no OAs are reachable
        HashMap<Node, HashSet<String>> dc = getBorderOas(user);
        if(dc.isEmpty()){
            return entry;
        }

        HashMap<Node, HashMap<Node, HashSet<String>>> D = new HashMap<>();

        for(Node pc : pcs){
            HashMap<Node, HashSet<String>> pcMap = new HashMap<>();
            pcMap.put(pc, new HashSet<>());
            D.put(pc, pcMap);
        }

        dfs(target, D, dc);

        //for every pc the object reaches check to see if they have a common access1 right.
        HashMap<Node, HashSet<String>> pcMap = D.get(target);
        boolean addOps = true;
        for(Node pc : pcMap.keySet()){
            //if there are permissions in the super pc then they apply to all pcs
            if(pc.getName().equals(Constants.SUPER_PC_NAME)) {
                ops.addAll(pcMap.get(pc));
                break;
            }

            if(addOps){
                ops.addAll(pcMap.get(pc));
                addOps = false;
            }else{
                ops.retainAll(pcMap.get(pc));
            }
        }

        //put the target node and the operations the user is allowed into the map
        entry = new PmAccessEntry(user, ops);

        return entry;
    }

    /**
     * Get all of the objects a user has access1 to, as well as the access1 rights for each object
     * @param user The user
     * @return A Map with Nodes as the keys and a HashSets of access1 rights as the values
     */
    public synchronized List<PmAccessEntry> getAccessibleNodes(Node user){
        //Node->{ops}
        List<PmAccessEntry> accessibleObjects = new ArrayList<>();

        //get policy classes
        HashSet<Node> pcs = getPolicyClasses();

        //get border nodes.  Can be OA or UA.  Return empty set if no OAs are reachable
        HashMap<Node, HashSet<String>> dc = getBorderOas(user);
        if(dc.isEmpty()){
            return accessibleObjects;
        }

        Node vNode = createVNode(dc);

        HashMap<Node, HashMap<Node, HashSet<String>>> D = new HashMap<>();

        for(Node pc : pcs){
            HashMap<Node, HashSet<String>> pcMap = new HashMap<>();
            pcMap.put(pc, new HashSet<>());
            D.put(pc, pcMap);
        }

        Set<Node> objects = graph.getAscesndants(vNode);

        for(Node v : objects){
            dfs(v, D, dc);

            //for every pc the object reaches check to see if they have a common access1 right.
            HashSet<String> finalOps = new HashSet<>();
            HashMap<Node, HashSet<String>> pcMap = D.get(v);
            boolean addOps = true;
            for(Node pc : pcMap.keySet()){
                //if there are permissions in the super pc then they apply to all pcs
                if(pc.getName().equals(Constants.SUPER_PC_NAME)) {
                    finalOps.addAll(pcMap.get(pc));
                    break;
                }

                if(addOps){
                    finalOps.addAll(pcMap.get(pc));
                    addOps = false;
                }else{
                    finalOps.retainAll(pcMap.get(pc));
                }
            }
            if(!finalOps.isEmpty()) {
                accessibleObjects.add(new PmAccessEntry(v, finalOps));
            }
        }

        graph.deleteNode(vNode);

        return accessibleObjects;
    }

    private synchronized Node createVNode(HashMap<Node, HashSet<String>> dc) {
        long id = new Random().nextLong();
        Node vNode = new Node(id, "VNODE", NodeType.OA);
        graph.addNode(vNode);
        for(Node node : dc.keySet()){
            graph.addEdge(node, vNode, new Assignment<>(node, vNode));
        }
        return vNode;
    }

    /**
     * Get the Users and their access1 rights for a specific node
     * @param target The node to get the Users and their access1 rights for
     * @return A Map with User Nodes as the keys and HashSets of access1 rights as the values
     */
    public List<PmAccessEntry> getUsersWithAccessOn(Node target){
        //user->ops
        List<PmAccessEntry> entries = new ArrayList<>();

        HashSet<Node> users = getUsers();

        //get policy classes
        HashSet<Node> pcs = getPolicyClasses();

        for(Node user : users) {
            //get border nodes.  Can be OA or UA.  Return empty set if no OAs are reachable
            HashMap<Node, HashSet<String>> dc = getBorderOas(user);
            if (dc.isEmpty()) {
                return entries;
            }

            HashMap<Node, HashMap<Node, HashSet<String>>> D = new HashMap<>();

            for (Node pc : pcs) {
                HashMap<Node, HashSet<String>> pcMap = new HashMap<>();
                pcMap.put(pc, new HashSet<>());
                D.put(pc, pcMap);
            }

            dfs(target, D, dc);

            //for every pc the object reaches check to see if they have a common access1 right.
            HashMap<Node, HashSet<String>> pcMap = D.get(target);
            HashSet<String> ops = new HashSet<>();
            boolean addOps = true;
            for (Node pc : pcMap.keySet()) {
                //if there are permissions in the super pc then they apply to all pcs
                if(pc.getName().equals(Constants.SUPER_PC_NAME)) {
                    ops.addAll(pcMap.get(pc));
                    break;
                }

                if (addOps) {
                    ops.addAll(pcMap.get(pc));
                    addOps = false;
                } else {
                    ops.retainAll(pcMap.get(pc));
                }
            }

            //put the target node and the operations the user is allowed into the map
            if(!ops.isEmpty()) {
                entries.add(new PmAccessEntry(user, ops));
            }
        }
        return entries;
    }

    /**
     * Get the children of a node that a user has access1 to and the access1 rights that user has on the children
     * @param user The user
     * @param target The Node to get the accessible children for
     * @return a map with the child Nodes as the keys and the HashSets of access1 rights as the values
     */
    public synchronized List<PmAccessEntry> getAccessibleChildrenOf(Node target, Node user){
        //Node->{ops}
        List<PmAccessEntry> accessibleObjects = new ArrayList<>();

        //get policy classes
        HashSet<Node> pcs = getPolicyClasses();

        //get border nodes.  Can be OA or UA.  Return empty set if no OAs are reachable
        HashMap<Node, HashSet<String>> dc = getBorderOas(user);
        if(dc.isEmpty()){
            return accessibleObjects;
        }

        Node vNode = createVNode(dc);

        HashMap<Node, HashMap<Node, HashSet<String>>> D = new HashMap<>();

        for(Node pc : pcs){
            HashMap<Node, HashSet<String>> pcMap = new HashMap<>();
            pcMap.put(pc, new HashSet<>());
            D.put(pc, pcMap);
        }

        Set<Node> objects = graph.getChildren(target);
        for(Node v : objects){
            dfs(v, D, dc);

            //for every pc the object reaches check to see if they have a common access1 right.
            HashSet<String> finalOps = new HashSet<>();
            HashMap<Node, HashSet<String>> pcMap = D.get(v);
            boolean addOps = true;
            for(Node pc : pcMap.keySet()){
                //if there are permissions in the super pc then they apply to all pcs
                if(pc.getName().equals(Constants.SUPER_PC_NAME)) {
                    finalOps.addAll(pcMap.get(pc));
                    break;
                }

                if(addOps){
                    finalOps.addAll(pcMap.get(pc));
                    addOps = false;
                }else{
                    finalOps.retainAll(pcMap.get(pc));
                }
            }
            if(!finalOps.isEmpty()) {
                accessibleObjects.add(new PmAccessEntry(v, finalOps));
            }
        }

        graph.deleteNode(vNode);

        return accessibleObjects;
    }

    private synchronized HashMap<Node, HashSet<String>> getBorderOas(Node user){
        HashMap<Node, HashSet<String>> d = new HashMap<>();
        HashSet<Assignment> uaEdges = new HashSet<>();
        Set<Assignment> edges = graph.outgoingEdgesOf(user);
        uaEdges.addAll(edges);
        while(!uaEdges.isEmpty()){
            Assignment edge = uaEdges.iterator().next();
            if(edge instanceof Association){
                Association assoc = (Association) edge;
                HashSet ops = d.get(edge.getParent());
                if(ops == null) {
                    ops = assoc.getOps();
                }

                if(ops != null) {
                    d.put(edge.getParent(), ops);
                }
            }

            Set<Assignment> newEdges = graph.outgoingEdgesOf(edge.getParent());

            uaEdges.addAll(newEdges);
            uaEdges.remove(edge);
        }

        return d;
    }

    private synchronized void dfs(Node w, HashMap<Node, HashMap<Node, HashSet<String>>> D, HashMap<Node, HashSet<String>> dc){
        D.put(w, new HashMap<>());
        //for loop through nodes
        //if node is not in D, recrusive call to dfs

        Set<Assignment> assignments = graph.outgoingEdgesOf(w);

        for(Assignment edge : assignments){
            if(edge instanceof Association){
                continue;
            }

            Node node = edge.getParent();
            if(!D.containsKey(node)){
                dfs(node, D, dc);
            }

            HashMap<Node, HashSet<String>> pcSet = D.get(node);
            for(Node pc : pcSet.keySet()){
                HashSet<String> ops = pcSet.get(pc);
                //HashSet<String> ops = pcSet.computeIfAbsent(pcId, k -> new HashSet<>());
                HashSet<String> exOps = D.get(w).computeIfAbsent(pc, k -> new HashSet<>());
                exOps.addAll(ops);
            }
        }

        if(dc.containsKey(w)){
            HashMap<Node, HashSet<String>> pcSet = D.get(w);
            for(Node pcId : pcSet.keySet()){
                HashSet<String> ops = dc.get(w);
                D.get(w).get(pcId).addAll(ops);
            }
        }

    }

    private synchronized HashSet<Node> getPolicyClasses() {
        return new HashSet<>(graph.getNodesOfType(NodeType.PC));
    }

    private synchronized HashSet<Node> getUsers() {
        return new HashSet<>(graph.getNodesOfType(NodeType.U));
    }

    /**
     * Get a list of all the prohibitions
     * @return a list of Prohibition objects
     */
    public synchronized List<Prohibition> getProhibitions(){
        return prohibitions;
    }

    /**
     * Add a prohibitions to the list
     * @param prohibition the Prohibition to add
     */
    public synchronized void addProhibition(Prohibition prohibition){
        prohibitions.add(prohibition);
    }

    /**
     * Remove a prohibitions
     * @param prohibitionName the name of the prohibitions to remove
     */
    public synchronized void removeProhibition(String prohibitionName){
        prohibitions.removeIf(prohibition -> prohibition.getName().equals(prohibitionName));
    }

    /**
     * Get the Prohibition object identified by the given prohibitions name
     * @param prohibitionName the name of the prohibitions to get
     * @return A Prohibition object with the specified name
     */
    public synchronized Prohibition getProhibition(String prohibitionName) {
        for (Prohibition prohibition : prohibitions) {
            if(prohibition.getName().equals(prohibitionName)){
                return prohibition;
            }
        }

        return null;
    }

    /**
     * Get the operations that are prohibited for a user
     * @param targetId the ID of the resource
     * @param subjectId the ID of the user/user attribute/process
     * @return The set of prohibited operations for the subject on the resource
     */
    public HashSet<String> getProhibitedOps(long targetId, long subjectId){
        HashSet<String> prohibitedOps = new HashSet<>();
        for(Prohibition prohibition : prohibitions){
            boolean subjectInDeny = (prohibition.getSubject().getSubjectId()==subjectId) ||
                    graph.getAscesndants(prohibition.getSubject().getSubjectId()).contains(graph.getNode(subjectId));
            if(subjectInDeny){
                boolean inter = prohibition.isIntersection();
                List<ProhibitionRes> resources = prohibition.getResources();

                HashMap<ProhibitionRes, HashSet<Node>> drAscendants = new HashMap<>();
                HashSet<Node> nodes = new HashSet<>();
                for (ProhibitionRes dr : resources) {
                    HashSet<Node> ascendants = graph.getAscesndants(dr.getResourceId());
                    drAscendants.put(dr, ascendants);
                    nodes.addAll(ascendants);
                }

                boolean addOps = false;
                if(inter) {
                    for (ProhibitionRes dr : drAscendants.keySet()) {
                        if (dr.isCompliment()) {
                            nodes.removeAll(drAscendants.get(dr));
                        }
                    }
                    if (nodes.contains(graph.getNode(targetId))) {
                        addOps = true;
                    }
                }else{
                    addOps = true;
                    for (ProhibitionRes dr : drAscendants.keySet()) {
                        HashSet<Node> ascs = drAscendants.get(dr);
                        if (dr.isCompliment()) {
                            if(ascs.contains(graph.getNode(targetId))){
                                addOps = false;
                            }
                        }else{
                            if(!ascs.contains(graph.getNode(targetId))){
                                addOps = false;
                            }
                        }
                    }
                }

                if(addOps){
                    prohibitedOps.addAll(prohibition.getOperations());
                }
            }
        }

        return prohibitedOps;
    }
}
