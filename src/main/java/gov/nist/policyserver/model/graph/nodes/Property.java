package gov.nist.policyserver.model.graph.nodes;

import gov.nist.policyserver.exceptions.InvalidPropertyException;

import java.io.Serializable;

public class Property  implements Serializable {
	private String key;
	private String value;
	public Property(){}
	public Property(String key, String value) throws InvalidPropertyException {
		if(key == null || value == null){
			throw new InvalidPropertyException(key, value);
		}
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean equals(Object o){
		if(o instanceof Property){
			Property prop = (Property)o;
			return this.getKey().equals(prop.getKey()) &&
					this.getValue().equals(prop.getValue());
		}
		return false;
	}

    public boolean isValid() {
		return key != null && value != null;
    }

    public String toString() {
	    return key + "=" + value;
    }
}
