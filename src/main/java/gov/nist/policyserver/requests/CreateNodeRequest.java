package gov.nist.policyserver.requests;

import gov.nist.policyserver.model.graph.nodes.Property;

public class CreateNodeRequest {
    String     name;
    String     type;
    String     description;
    Property[] properties;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }
}
