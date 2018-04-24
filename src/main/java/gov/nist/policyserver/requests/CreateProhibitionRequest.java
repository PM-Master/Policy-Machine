package gov.nist.policyserver.requests;

import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;

public class CreateProhibitionRequest {
    public String                name;
    public boolean               intersection;
    public String[]              operations;
    public ProhibitionRes[]      resources;
    public ProhibitionSubject    subject;

    public String getName() {
        return name;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public String[] getOperations() {
        return operations;
    }

    public ProhibitionRes[] getResources() {
        return resources;
    }

    public ProhibitionSubject getSubject() {
        return subject;
    }
}
