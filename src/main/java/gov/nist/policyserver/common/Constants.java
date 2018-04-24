package gov.nist.policyserver.common;

public class Constants {
    //operations
    public static final String FILE_WRITE = "file write";
    public static final String FILE_READ = "file read";
    public static final String CREATE_OBJECT_ATTRIBUTE = "create object attribute";
    public static final String ASSIGN_OBJECT_ATTRIBUTE = "assign object attribute";
    public static final String CREATE_NODE = "create node";
    public static final String DELETE_NODE = "delete node";
    public static final String UPDATE_NODE = "update node";
    public static final String ASSIGN_TO = "assign to";
    public static final String ASSIGN = "assign";
    public static final String DEASSIGN = "deassign";
    public static final String DEASSIGN_FROM = "deassign from";
    public static final String CREATE_ASSOCIATION = "create association";
    public static final String UPDATE_ASSOCIATION = "update association";
    public static final String DELETE_ASSOCIATION = "delete association";
    public static final String GET_ASSOCIATIONS = "get associations";
    public static final String ALL_OPERATIONS = "*";
    public static final String ANY_OPERATIONS = "any";
    public static final String GET_PERMISSIONS = "get permissions";
    public static final String GET_ACCESSIBLE_CHILDREN = "get accessible children";
    public static final String GET_PROHIBITED_OPS = "get prohibited ops";
    public static final String GET_ACCESSIBLE_NODES = "get accessible nodes";

    //connector
    public static final String CONNECTOR_NAME = "PM";
    public static final String CONNECTOR_NAMESPACE = "connector";

    //super pc
    public static final String SUPER_PC_NAME = "Super PC";

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
    public static final int ERR_ACCESS_DENIED                       = 6024;
    public static final int ERR_NODE_ID_EXISTS                      = 6025;
    public static final int ERR_ASSOCIATION_EXISTS                  = 6025;
    public static final int ERR_MISSING_PERMISSIONS                 = 6025;
    public static final int ERR_SESSION_USER_NOT_FOUND              = 6026;

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
    public static final String PM_EVENT_OBJECT_CREATE = "object create";
    public static final String PM_EVENT_OBJECT_DELETE  = "object delete";
    public static final String PM_EVENT_OBJECT_READ    = "object read";
    public static final String PM_EVENT_OBJECT_WRITE   = "object write";
    public static final String PM_EVENT_USER_CREATE    = "user create";
    public static final String PM_EVENT_SESSION_CREATE = "session create";
    public static final String PM_EVENT_SESSION_DELETE = "session delete";
    public static final String PM_EVENT_OBJECT_SEND    = "object send";

    public static final int HASH_LENGTH = 163;
    public static final String PASSWORD_PROPERTY        = "password";
    public static final String DESCRIPTION_PROPERTY     = "description";
    public static final String NAMESPACE_PROPERTY       = "namespace";
    public static final String COLUMN_INDEX_PROPERTY    = "column_index";
    public static final String ORDER_BY_PROPERTY        = "order_by";
    public static final String ROW_INDEX_PROPERTY       = "row_index";
    public static final String SESSION_USER_ID_PROPERTY = "user_id";
    public static final String SCHEMA_COMP_PROPERTY        = "schema_comp";
    public static final String SCHEMA_COMP_SCHEMA_PROPERTY = "schema";
    public static final String SCHEMA_COMP_TABLE_PROPERTY  = "table";
    public static final String SCHEMA_COMP_ROW_PROPERTY    = "row";
    public static final String SCHEMA_COMP_COLUMN_PROPERTY = "col";
    public static final String SCHEMA_COMP_CELL_PROPERTY   = "cell";
    public static final String SCHEMA_NAME_PROPERTY        = "schema";
    public static final String COLUMN_CONTAINER_NAME       = "Columns";
    public static final String ROW_CONTAINER_NAME          = "Rows";
    public static final boolean INHERIT_DEFAULT                = true;

}
