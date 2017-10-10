package gov.nist.policyserver.model.prohibitions;

import java.io.Serializable;

public class ProhibitionRes implements Serializable {
    long resourceId;
    boolean compliment;

    public ProhibitionRes(){}

    public ProhibitionRes(long resourceId, boolean compliment) {
        this.resourceId = resourceId;
        this.compliment = compliment;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long ProhibitionResourceId) {
        this.resourceId = ProhibitionResourceId;
    }

    public boolean isCompliment() {
        return compliment;
    }

    public void setCompliment(boolean compliment) {
        this.compliment = compliment;
    }

    public int hashCode(){
        return (int) resourceId;
    }

    public boolean equals(Object o){
        if(o instanceof ProhibitionRes){
            ProhibitionRes n = (ProhibitionRes) o;
            return this.resourceId == n.resourceId;
        }
        return false;
    }
}
