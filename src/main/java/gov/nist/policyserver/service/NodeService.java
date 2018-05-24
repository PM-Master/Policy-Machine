package gov.nist.policyserver.service;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.model.graph.nodes.Property;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Constants.*;
import static gov.nist.policyserver.dao.DAO.getDao;

public class NodeService extends Service{

    public NodeService() throws ConfigurationException {
        super();
    }

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

    public HashSet<Node> getNodes(String namespace, String name, String type, List<Property> properties)
            throws InvalidNodeTypeException {
        NodeType nodeType = (type != null) ? NodeType.toNodeType(type) : null;

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
        if(properties != null && !properties.isEmpty()) {
            nodes.removeIf(node -> {
                for (Property prop : properties) {
                    if(node.hasProperty(prop)) {
                        return false;
                    }
                }
                return true;
            });
        }

        return nodes;
    }

    public Node getNode(String namespace, String name, String type, List<Property> properties)
            throws InvalidNodeTypeException, UnexpectedNumberOfNodesException {
        NodeType nodeType = (type != null) ? NodeType.toNodeType(type) : null;

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
        if(properties != null && !properties.isEmpty()) {
            nodes.removeIf(node -> {
                for (Property prop : properties) {
                    if(node.hasProperty(prop)) {
                        return false;
                    }
                }
                return true;
            });
        }

        if(nodes.size() != 1) {
            throw new UnexpectedNumberOfNodesException();
        }

        return nodes.iterator().next();
    }

    public HashSet<Node> getNodes(HashSet<Node> nodes, String namespace, String name, String type, String key, String value)
            throws InvalidNodeTypeException, InvalidPropertyException {
        NodeType nodeType = (type != null) ? NodeType.toNodeType(type) : null;
        Property property = (key==null||value==null)?null : new Property(key, value);

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

    public Node getNode(String name, String type, String properties) throws InvalidPropertyException, InvalidNodeTypeException, UnexpectedNumberOfNodesException {
        //get target node
        //get properties
        List<Property> propList = new ArrayList<>();
        if(properties != null) {
            String[] propertiesArr = properties.split(",\\s*");
            for (String prop : propertiesArr) {
                String[] split = prop.split("=");
                if (split.length == 2) {
                    propList.add(new Property(split[0], split[1]));
                }
            }
        }
        return getNode(null, name, type, propList);
    }

    public Node createNode(long id, String name, String type, Property[] properties)
            throws NullNameException, NullTypeException, InvalidNodeTypeException,
            InvalidPropertyException, NodeNameExistsInNamespaceException, DatabaseException,
            ConfigurationException, NodeNameExistsException, NodeIdExistsException,
            NodeNotFoundException {
        //check name and type are not null
        if(name == null){
            throw new NullNameException();
        }
        if(type == null){
            throw new NullTypeException();
        }

        if(id != 0) {
            //check if ID exists
            try {
                Node node = getNode(id);
                throw new NodeIdExistsException(id, node);
            }
            catch (NodeNotFoundException e) {/*expected exception*/}
        }

        boolean checkDefault = true;
        //check this name will be the only one in the namespace
        if(properties != null) {
            for (Property property : properties) {
                //check if namespace property exists
                if (property.isValid() && property.getKey().equals(NAMESPACE_PROPERTY)) {
                    HashSet<Node> nodes = getNodes(property.getValue(), name, null, null, null);

                    if (nodes.size() > 0) {
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
        Node newNode = getDao().createNode(id, name, nt);

        //add the node to the nodes
        graph.addNode(newNode);

        //assign node to connector
        if(newNode.getId() > 0) {
            AssignmentService assignmentService = new AssignmentService();
            try {
                assignmentService.createAssignment(newNode.getId(), getConnector().getId());
            }
            catch (AssignmentExistsException e) {
                //a new node should not be assigned to the connector yet
                //ignore
            }
        }

        //add properties to the node
        try {
            newNode = addNodeProperties(newNode, properties);
        }
        catch (PropertyNotFoundException e) {
            e.printStackTrace();
        }

        return newNode;
    }

    private Node addNodeProperties(Node node, Property[] properties) throws NodeNotFoundException, DatabaseException, ConfigurationException, InvalidPropertyException, PropertyNotFoundException {
        if(properties != null) {
            for (Property property : properties) {
                if(property.isValid()) {
                    try {
                        if(node.hasProperty(property.getKey())) {
                            updateNodeProperty(node.getId(), property.getKey(), property.getValue());
                        } else {
                            if(property.getKey().equals(PASSWORD_PROPERTY)) {
                                //check ic password is already hashed, and hash it if not
                                //this will occur when loading a configuration
                                if(property.getValue().length() != HASH_LENGTH) {
                                    String hash = generatePasswordHash(property.getValue());
                                    property.setValue(hash);
                                }
                            }
                            addNodeProperty(node.getId(), property.getKey(), property.getValue());
                        }
                    }
                    catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        throw new InvalidPropertyException("Could not add password property. Node was created anyways.");
                    }
                }
            }
        }

        return node;
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

    public Node updateNode(long nodeId, String name, Property[] properties) throws NodeNotFoundException, DatabaseException, ConfigurationException, InvalidPropertyException, PropertyNotFoundException {
        //check node exists
        Node node = getNode(nodeId);

        //update node in the database
        getDao().updateNode(nodeId, name);

        //update node in graph
        graph.updateNode(nodeId, name);

        //delete node properties
        deleteNodeProperties(nodeId);

        //add the new properties
        addNodeProperties(node, properties);

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

    private void deleteNodeProperties(long nodeId) throws NodeNotFoundException, ConfigurationException, DatabaseException {
        List<Property> props = getNodeProperties(nodeId);

        for(Property property : props) {
            getDao().deleteNodeProperty(nodeId, property.getKey());
        }

        graph.deleteNodeProperties(nodeId);
    }

    public void updateNodeProperty(long nodeId, String key, String value) throws NodeNotFoundException, PropertyNotFoundException, ConfigurationException, DatabaseException, InvalidKeySpecException, NoSuchAlgorithmException {
        //check if the property exists
        getNodeProperty(nodeId, key);

        if(key.equals(PASSWORD_PROPERTY)) {
            value = generatePasswordHash(value);
        }

        //update the property
        getDao().updateNodeProperty(nodeId, key, value);

        //update property in graph
        graph.updateNodeProperty(nodeId, key, value);
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
        //TODO

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
