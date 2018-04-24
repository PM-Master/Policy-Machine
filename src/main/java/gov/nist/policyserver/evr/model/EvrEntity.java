package gov.nist.policyserver.evr.model;

import gov.nist.policyserver.evr.exceptions.InvalidEntityException;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.Property;

import java.util.ArrayList;
import java.util.List;

public class EvrEntity {
    private Node           node;
    private String         name;
    private String         type;
    private List<Property> properties;
    private EvrFunction    function;
    private EvrProcess     process;
    private List<EvrEntity> evrEntityList;
    private boolean compliment;

    public EvrEntity() {

    }

    public boolean isClass() {
        if(type == null) {
            return false;
        }

        return type.equals("class");
    }

    //node
    public EvrEntity(Node node) {
        this.node = node;
    }

    public Node getNode() throws InvalidEntityException {
        if(node == null) {
            throw new InvalidEntityException("This entity is not a Node");
        }
        return node;
    }

    public boolean isNode() {
        return node != null;
    }

    //function
    public EvrEntity(EvrFunction function) {
        this.function = function;
    }

    public EvrFunction getFunction() throws InvalidEntityException {
        if(function == null) {
            throw new InvalidEntityException("This entity is not a function");
        }
        return this.function;
    }

    public boolean isFunction() {
        return function != null;
    }

    //node
    public EvrEntity(String name, String type, List<Property> properties, boolean comp) {
        this.name = name;
        this.type = type;
        this.properties = properties;
        this.compliment = comp;
    }

    public boolean isCompliment() {
        return compliment;
    }

    public void setCompliment(boolean compliment) {
        this.compliment = compliment;
    }

    public String getName() {
        if(name == null) {
            if(isNode()){
                return node.getName();
            }
        }

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        if(type == null) {
            if(isNode()){
                return node.getType().toString();
            }
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Property> getProperties() {
        if(properties == null) {
            if(isNode()) {
                return node.getProperties();
            }
        }

        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public boolean isEvrNode() {
        return name != null || type != null || properties != null;
    }

    //process
    public EvrEntity(EvrProcess process) {
        this.process = process;
    }

    public EvrProcess getProcess() {
        return process;
    }

    public boolean isProcess() {
        return process != null;
    }

    //value
    public boolean isValue() {
        return name != null && type == null;
    }

    public boolean isAny() {
        return node == null
                && name == null
                && type == null
                && (properties == null || properties.size() == 0)
                && process == null
                && function == null;
    }

    //list
    public EvrEntity(List<EvrEntity> evrEntityList) {
        this.evrEntityList = evrEntityList;
    }

    public void addEntity(EvrEntity evrEntity) {
        if(evrEntityList == null) {
            evrEntityList = new ArrayList<>();
        }

        evrEntityList.add(evrEntity);
    }

    public List<EvrEntity> getEntityList() {
        return evrEntityList;
    }

    public boolean isList() {
        return evrEntityList != null;
    }

    public boolean equals(Object o) {
        if(!(o instanceof EvrEntity)) {
            return false;
        }

        EvrEntity entity = (EvrEntity) o;

        if(this.isAny() && entity.isAny()) {
            return true;
        }

        //check node
        if(this.isNode()) {
            return this.getName().equals(entity.getName()) &&
                    this.getType().equals(entity.getType()) &&
                    this.getProperties().equals(entity.getProperties());
        }

        //check function
        if(this.isFunction()) {
            try {
                return this.getFunction().equals(entity.getFunction());
            }
            catch (InvalidEntityException e) {
                return false;
            }
        }

        //check process
        if(this.isProcess()) {
            return this.getProcess().equals(entity.getProcess());
        }

        return false;
    }
}
