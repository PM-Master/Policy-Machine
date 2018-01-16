package gov.nist.policyserver.model.graph.nodes;

import gov.nist.policyserver.exceptions.InvalidPropertyException;
import gov.nist.policyserver.exceptions.PropertyNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node implements Serializable{
    private String         namespace;
    private long           id;
    private String         name;
    private NodeType       type;
    private String         description;
    private List<Property> properties;

    public Node(){}

    public Node (String name, NodeType type){
        if(name == null){
            throw new IllegalArgumentException("The name of a node cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("The type of a node annot be null");
        }

        this.name = name;
        this.type = type;
        description = "";
        properties = new ArrayList<>();
    }

    public Node (long id, String name, NodeType type){
        if(name == null){
            throw new IllegalArgumentException("The name of a node cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("The type of a node annot be null");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        description = "";
        properties = new ArrayList<>();
    }

    public Node (long id, String name, NodeType type, String description){
        if(name == null){
            throw new IllegalArgumentException("The name of a node cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("The type of a node annot be null");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = new ArrayList<>();
    }

    public Node (String name, NodeType type, String description){
        if(name == null){
            throw new IllegalArgumentException("The name of a node cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("The type of a node cannot be null");
        }

        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = new ArrayList<>();
    }

    public Node (long id, String name, NodeType type, String description, Property[] properties){
        if(name == null){
            throw new IllegalArgumentException("The name of a node cannot be null");
        }
        if(type == null){
            throw new IllegalArgumentException("The type of a node cannot be null");
        }

        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.properties = Arrays.asList(properties);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public boolean hasProperty(Property property){
        return properties.contains(property);
    }

    public boolean hasProperty(String key){
        for(Property prop : properties){
            if(prop.getKey().equals(key)){
                return true;
            }
        }
        return false;
    }

    public Property getProperty(String key) throws PropertyNotFoundException {
        for(Property prop : properties){
            if(prop.getKey().equals(key)){
                return prop;
            }
        }
        throw new PropertyNotFoundException(this.id, key);
    }

    public void addProperty(String key, String value) throws InvalidPropertyException {
        this.properties.add(new Property(key,value));
    }

    public void addProperty(Property prop){
        this.properties.add(prop);
    }

    public void deleteProperty(String key) {
        properties.removeIf(property -> property.getKey().equals(key));
    }

    public int hashCode(){
        return (int) id;
    }

    public boolean equals(Object o){
        if(o instanceof Node){
            Node n = (Node) o;
            return this.id == n.id;
        }
        return false;
    }
}