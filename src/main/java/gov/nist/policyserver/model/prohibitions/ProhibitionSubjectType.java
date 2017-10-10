package gov.nist.policyserver.model.prohibitions;

import gov.nist.policyserver.exceptions.InvalidProhibitionSubjectTypeException;

import java.io.Serializable;

public enum ProhibitionSubjectType  implements Serializable {
    UA("UA"),
    U("U"),
    P("P");

    String value;
    ProhibitionSubjectType(String value){
        this.value = value;
    }
    public String toString(){
        return value;
    }

    public static ProhibitionSubjectType toProhibitionSubjectType(String subjectType) throws InvalidProhibitionSubjectTypeException {
        if(subjectType == null){
            throw new InvalidProhibitionSubjectTypeException(subjectType);
        }
        switch (subjectType.toUpperCase()){
            case "UA":
                return ProhibitionSubjectType.UA;
            case "U":
                return ProhibitionSubjectType.U;
            case "P":
                return ProhibitionSubjectType.P;
            default:
                throw new InvalidProhibitionSubjectTypeException(subjectType);
        }
    }
}
