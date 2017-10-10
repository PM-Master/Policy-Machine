package gov.nist.policyserver.common;

public class Constants {
    //error codes
    //web services
    public static final int ERR_ASSIGNMENT_DOES_NOT_EXIST           = 6001;
    public static final int ERR_PROHIBITION_NAME_EXISTS             = 6002;
    public static final int ERR_PROHIBITION_RESOURCE_EXISTS         = 6003;
    public static final int ERR_NULL_ID                             = 6004;
    public static final int ERR_ASSIGNMENT_EXISTS                   = 6005;
    public static final int ERR_INVALID_PROPERTY                    = 6006;
    public static final int ERR_INVALID_PROHIBITION_SUBJECTTYPE     = 6007;
    public static final int ERR_NODE_NOT_FOUND                      = 6008;
    public static final int ERR_NO_USER_PARAMETER                   = 6009;
    public static final int ERR_ASSOCIATION_DOES_NOT_EXIST          = 6010;
    public static final int ERR_NULL_NAME                           = 6011;
    public static final int ERR_PROHIBITION_RESOURCE_DOES_NOT_EXIST = 6012;
    public static final int ERR_NO_SUBJECT_PARAMETER                = 6013;
    public static final int ERR_CONFIGURATION                       = 6014;
    public static final int ERR_PROPERTY_NOT_FOUND                  = 6015;
    public static final int ERR_NAME_IN_NAMESPACE_NOT_FOUND         = 6016;
    public static final int ERR_NODE_NAME_EXISTS                    = 6017;
    public static final int ERR_INVALID_NODETYPE                    = 6018;
    public static final int ERR_PROHIBITION_SUBJECT_DOES_NOT_EXIST  = 6019;
    public static final int ERR_NULL_TYPE                           = 6020;
    public static final int ERR_NODE_NAME_EXISTS_IN_NAMESPACE       = 6021;
    public static final int ERR_PROHIBITION_DOES_NOT_EXIST          = 6022;
    public static final int ERR_SESSION_DOES_NOT_EXIST              = 6023;
    public static final int ERR_NEO                                 = 7000;
    public static final int ERR_MYSQL                               = 8000;
    public static final int SUCCESS                                 = 9000;


    public static final long CONNECTOR_ID = 1L;
    public static final long NO_USER      = -1;
    public static final String NEO4J      = "neo4j";

    //obligations
    public static final String PM_UNKNOWN = "k";
    public static final String PM_RULE = "rule";
    public static final String PM_LABEL = "l";
    public static final String PM_EVENT_CREATE = "create";
    public static final String PM_EVENT_DELETE = "delete";
    // Events.
    public static final String PM_EVENT_OBJECT_CREATE = "Object create";
    public static final String PM_EVENT_OBJECT_DELETE  = "Object delete";
    public static final String PM_EVENT_OBJECT_READ    = "Object read";
    public static final String PM_EVENT_OBJECT_WRITE   = "Object write";
    public static final String PM_EVENT_USER_CREATE    = "User create";
    public static final String PM_EVENT_SESSION_CREATE = "Session create";
    public static final String PM_EVENT_SESSION_DELETE = "Session delete";
    public static final String PM_EVENT_OBJECT_SEND    = "Object send";

    public static final String NAMESPACE_PROPERTY       = "namespace";
    public static final String COLUMN_INDEX_PROPERTY    = "column_index";
    public static final String ORDER_BY_PROPERTY        = "order_by";
    public static final String ROW_INDEX_PROPERTY       = "row_index";
    public static final String SESSION_USER_ID_PROPERTY = "user_id";
}
