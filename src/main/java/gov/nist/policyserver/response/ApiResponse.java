package gov.nist.policyserver.response;

import gov.nist.policyserver.exceptions.PmException;

import javax.ws.rs.core.Response;

import static gov.nist.policyserver.common.Constants.SUCCESS;

public class ApiResponse {
    private Integer code                                     = null;
    private String  message                                  = null;
    private Object  entity                                   = null;

    /*public static final String CLIENT_ERRMSG                      = "There was an error on the client side";
    public static final String SERVER_ERRMSG                      = "There was an error on the server side";
    public static final String NODE_NAME_EXISTS_ERRMSG            = "The server received a Node name that already exists.";
    public static final String ASSIGNMENT_EXISTS_ERRMSG           = "There is already an assignment between %d (start) and %d (end)";
    public static final String NO_ASSIGNMENT_EXISTS_ERRMSG        = "There is no assignment between %d (start) and %d (end)";
    public static final String DUPLICATE_NAME_IN_PATH_ERRMSG      = "Node with name '%s' already exists in the path";
    public static final String NODE_NOT_FOUND_ERRMSG              = "Node with id %d could not be found";
    public static final String NULL_ID_ERRMSG                     = "The server received a null id";
    public static final String NULL_NAME_ERRMSG                   = "The server received a null name";
    public static final String NULL_TYPE_ERRMSG                   = "The server received a null type";
    public static final String STARTUP_ERRMSG                     = "The server encountered an error on startup";
    public static final String NODE_NOT_CONNECTED_TO_GRAPH_ERRMSG = "The start node with id %d has not been connected to the nodes yet.  Connect the start node to the nodes to complete this assignment.";
    public static final String CREATE_NODE_ERR_MSG                = "Could not create node";
    public static final String NO_ACL_ENTRY_FOUND                 = "No ACL Entry found for this node";
    public static final String UPDATE_ACCESS_ERRMSG               = "Could not update access1";
    public static final String ASSOCIATION_DOES_NOT_EXIST         = "Could not update access1";
    public static final String PROP_NOT_FOUND_ERRMSG              = "Node with ID = %d does not have a property with key '%s'";*/

    public static final String DELETE_ASSIGNMENT_SUCCESS    = "Assignment was successfully deleted";
    public static final String POST_NODE_PROPERTY_SUCCESS   = "The property was successfully added to the node";
    public static final String DELETE_NODE_PROPERTY_SUCCESS = "The property was successfully deleted";
    public static final String DELETE_NODE_CHILDREN_SUCESS  = "The children of the node were all deleted";
    public static final String REMOVE_ACCESS_SUCCESS        = "Access successfully removed";
    public static final String CREATE_ASSIGNMENT_SUCCESS    = "Assignment was successfully created";
    public static final String GRANT_ACCESS_SUCCESS         = "Access successfully granted";
    public static final String UPDATE_ACCESS_SUCESS         = "Successfully updated access1";
    public static final String DELETE_NODE_SUCCESS          = "Node successfully deleted";
    public static final String PUT_NODE_SUCCESS             = "Node was successfully updated";
    public static final String CREATE_PROHIBITION_SUCCESS   = "The Prohibition was successfully created";
    public static final String DELETE_PROHIBITION_SUCCESS           = "Prohibition was deleted successfully";
    public static final String ADD_PROHIBITION_RESOURCE_SUCCESS     = "Resource was added to the prohibitions";
    public static final String REMOVE_PROHIBITION_RESOURCE_SUCCESS  = "The resource was successfully removed from the prohibitions";
    public static final String POST_PROHIBITION_SUBJECT_SUCCESS     = "The subject was successfully set for the prohibitions";
    public static final String ADD_PROHIBITION_OPS_SUCCESS          = "The operations were successfully added to the prohibitions";
    public static final String REMOVE_PROHIBITION_OP_SUCCESS        = "The operation was successfully removed from the prohibitions";
    public static final String DELETE_NODE_IN_NAMESPACE_SUCCESS     = "The node was successfully deleted";


    private static final String SUCCESS_MSG = "Success";

    public ApiResponse() {
    }

    public ApiResponse(Object entity) {
        this.code = SUCCESS;
        this.message = SUCCESS_MSG;
        this.entity = entity;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(PmException exception) {
        this.code = exception.getErrorCode();
        this.message = exception.getMessage();
    }

    public ApiResponse(Exception exception) {
        this.code = exception.hashCode();
        this.message = exception.toString();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Response toResponse() {
        return Response.ok()
                .entity(this)
                .build();
    }
}
