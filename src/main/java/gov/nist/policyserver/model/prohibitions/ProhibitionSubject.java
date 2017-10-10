package gov.nist.policyserver.model.prohibitions;

import java.io.Serializable;

public class ProhibitionSubject  implements Serializable {
    long subjectId;
    ProhibitionSubjectType subjectType;

    public ProhibitionSubject(){

    }

    public ProhibitionSubject(long subjectId, ProhibitionSubjectType subjectType) {
        this.subjectId = subjectId;
        this.subjectType = subjectType;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    public ProhibitionSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(ProhibitionSubjectType subjectType) {
        this.subjectType = subjectType;
    }
}
