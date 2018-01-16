package gov.nist.policyserver.service;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;

import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.dao.DAO.getDao;

public class NodeService extends Service{

    private static final String NAMESPACE_PROPERTY = "namespace";

    public NodeService() throws ConfigurationException {
        super();
    }


    /**
     * Search for nodes
     * @param namespace
     * @param name
     * @param type
     * @param key
     * @param value
     * @return A HashSet of nodes, if none are found it will be empty
     * @throws InvalidNodeTypeException
     * @throws InvalidPropertyException
     */
    public HashSet<Node> getNodes(String namespace, String name, String type, String key, String value)
            throws InvalidNodeTypeException, InvalidPropertyException {
        NodeType nodeType = (type != null) ? NodeType.toNodeType(type) : null;
        Property property = (key==null||value==null)?null : new Property(key, value);

        HashSet<Node> nodes = graph.getNodes();

        //check namespace match
        if(namespace != null){
            nodes.removeIf(node -> {
                try {
                    return !node.hasProperty(NAMESPACE_PROPERTY) || !node.getProperty(NAMESPACE_PROPERTY).getValue().equalsIgnoreCase(namespace);
                }
                catch (PropertyNotFoundException e) {
                    return true;
                }
            });
        }

        //check name match
        if(name != null){
            nodes.removeIf(node -> !node.getName().equals(name));
        }

        //check type match
        if(nodeType != null){
            nodes.removeIf(node -> !node.getType().equals(nodeType));
        }

        //check property match
        if(property != null) {
            nodes.removeIf(node -> !node.hasProperty(property));
        }

        return nodes;
    }

    public Node createNode(String name, String type, String description, Property[] properties)
            throws NullNameException, NullTypeException, InvalidNodeTypeException, InvalidPropertyException, NodeNameExistsInNamespaceException,
            DatabaseException, ConfigurationException, NodeNameExistsException {
        //check name and type are not null
        if(name == null){
            throw new NullNameException();
        }
        if(type == null){
            throw new NullTypeException();
        }

        boolean checkDefault = true;
        //check this name will be the only one in the namespace
        if(properties != null) {
            for (Property property : properties) {

                //check if namespace property exists
                if (property.getKey().equals(NAMESPACE_PROPERTY)) {
                    Node nodeInNamespace = null;
                    try {
                        nodeInNamespace = getNodeInNamespace(property.getValue(), name);                    }
                    catch (NameInNamespaceNotFoundException e) {
                        System.err.println(e.getMessage());
                    }

                    if (nodeInNamespace != null) {
                        throw new NodeNameExistsInNamespaceException(property.getValue(), name);
                    }

                    checkDefault = false;
                    break;
                }
            }
        }

        if(checkDefault){
            //check if name exists in the default namespace
            HashSet<Node> nodes = getNodes(null, name, type, null, null);
            if (!nodes.isEmpty()) {
                throw new NodeNameExistsException(name);
            }
        }

        //create node in database
        NodeType nt = NodeType.toNodeType(type);
        Node newNode = getDao().createNode(name, nt, description);

        //add the node to the nodes
        graph.addNode(newNode);

        //add properties to the node
        if(properties != null) {
            for (Property property : properties) {
                try {
                    addNodeProperty(newNode.getId(), property.getKey(), property.getValue());
                }
                catch (NodeNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return newNode;
    }

    public Node getNode(long nodeId)
            throws NodeNotFoundException {
        Node node = graph.getNode(nodeId);
        if(node == null){
            throw new NodeNotFoundException(nodeId);
        }

        return node;
    }

    public Node getNodeInNamespace(String namespace, String name)
            throws NameInNamespaceNotFoundException, InvalidNodeTypeException, InvalidPropertyException {
        HashSet<Node> nodes = getNodes(namespace, name, null, null, null);
        if(nodes.isEmpty()){
            throw new NameInNamespaceNotFoundException(namespace, name);
        }

        return nodes.iterator().next();
    }

    public void deleteNodeInNamespace(String namespace, String nodeName)
            throws InvalidNodeTypeException, NameInNamespaceNotFoundException, InvalidPropertyException, NodeNotFoundException, DatabaseException, ConfigurationException {
        //get the node in namespace
        Node node = getNodeInNamespace(namespace, nodeName);

        deleteNode(node.getId());
    }

    public Node updateNode(long nodeId, String name, String description) throws NodeNotFoundException, DatabaseException, ConfigurationException {
        //check node exists
        getNode(nodeId);

        //update node in the database
        getDao().updateNode(nodeId, name, description);

        //update node in nodes
        graph.updateNode(nodeId, name, description);

        return graph.getNode(nodeId);
    }

    public void deleteNode(long nodeId) throws NodeNotFoundException, DatabaseException, ConfigurationException {
        //check node exists
        getNode(nodeId);

        //delete node in db
        getDao().deleteNode(nodeId);

        //delete node in database
        graph.deleteNode(nodeId);
    }

    public List<Property> getNodeProperties(long nodeId) throws NodeNotFoundException {
        //get node
        Node node = getNode(nodeId);

        return node.getProperties();
    }

    public Node addNodeProperty(long nodeId, String key, String value) throws InvalidPropertyException, NodeNotFoundException, DatabaseException, ConfigurationException {
        Property prop = new Property(key, value);

        //check node exists
        Node node = getNode(nodeId);

        //add property to node in database
        getDao().addNodeProperty(nodeId, prop);

        //add property to node in nodes
        node.addProperty(prop);

        return node;
    }

    public Property getNodeProperty(long nodeId, String key) throws NodeNotFoundException, PropertyNotFoundException {
        //get node
        Node node = getNode(nodeId);

        //get node property
        return node.getProperty(key);
    }

    public void deleteNodeProperty(long nodeId, String key) throws NodeNotFoundException, PropertyNotFoundException, DatabaseException, ConfigurationException {
        //check if the property exists
        getNodeProperty(nodeId, key);

        //delete the node property
        getDao().deleteNodeProperty(nodeId, key);

        //delete node from the nodes
        graph.deleteNodeProperty(nodeId, key);
    }

    public HashSet<Node> getChildrenOfType(long nodeId, String childType) throws NodeNotFoundException, InvalidNodeTypeException {
        Node node = getNode(nodeId);

        HashSet<Node> children = graph.getChildren(node);
        HashSet<Node> retChildren = new HashSet<>();
        retChildren.addAll(children);
        if(childType != null) {
            NodeType nt = NodeType.toNodeType(childType);
            for (Node n : children) {
                if (!n.getType().equals(nt)) {
                    retChildren.remove(n);
                }
            }
        }
        return retChildren;
    }

    public void deleteNodeChildren(long nodeId, String childType) throws NodeNotFoundException, InvalidNodeTypeException, DatabaseException, ConfigurationException {
        HashSet<Node> children = getChildrenOfType(nodeId, childType);
        for(Node node : children){
            //delete node in db
            getDao().deleteNode(node.getId());

            //delete node in database
            graph.deleteNode(node.getId());
        }
    }

    public HashSet<Node> getParentsOfType(long nodeId, String parentType) throws InvalidNodeTypeException, NodeNotFoundException {
        Node node = getNode(nodeId);

        HashSet<Node> parents = graph.getParents(node);
        HashSet<Node> retParents = new HashSet<>();
        retParents.addAll(parents);
        if(parentType != null) {
            NodeType nt = NodeType.toNodeType(parentType);
            for (Node n : parents) {
                if (!n.getType().equals(nt)) {
                    retParents.remove(n);
                }
            }
        }
        return retParents;
    }
}
