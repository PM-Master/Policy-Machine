package gov.nist.policyserver.model.prohibitions;

import java.io.Serializable;

public class ProhibitionResource implements Serializable {
    long    resourceId;
    boolean complement;

    public ProhibitionResource(){}

    public ProhibitionResource(long resourceId, boolean complement) {
        this.resourceId = resourceId;
        this.complement = complement;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long ProhibitionResourceId) {
        this.resourceId = ProhibitionResourceId;
    }

    public boolean isComplement() {
        return complement;
    }

    public void setComplement(boolean complement) {
        this.complement = complement;
    }

    public int hashCode(){
        return (int) resourceId;
    }

    public boolean equals(Object o){
        if(o instanceof ProhibitionResource){
            ProhibitionResource n = (ProhibitionResource) o;
            return this.resourceId == n.resourceId;
        }
        return false;
    }
}
