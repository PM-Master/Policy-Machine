package gov.nist.policyserver.requests;

import gov.nist.policyserver.model.graph.nodes.Property;

public class CreateNodeRequest {
    long       id;
    String     name;
    String     type;
    Property[] properties;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }
}
