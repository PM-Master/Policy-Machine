package gov.nist.policyserver.requests;

public class PostSubjectToProhibitionRequest {
    long subjectId;
    String subjectType;
    public long getSubjectId(){
        return subjectId;
    }
    public String getSubjectType(){
        return subjectType;
    }
}
