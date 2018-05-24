package gov.nist.policyserver.kernel;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.graph.nodes.NodeType;
import gov.nist.policyserver.requests.AssignmentRequest;
import gov.nist.policyserver.requests.CreateNodeRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AnalyticsService;
import gov.nist.policyserver.service.AssignmentService;
import gov.nist.policyserver.service.NodeService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Constants.FILE_WRITE;

@Path("kernel")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PmKernelResource {

    NodeService       nodeService       = new NodeService();
    AnalyticsService  analyticsService  = new AnalyticsService();
    AssignmentService assignmentService = new AssignmentService();

    public PmKernelResource() throws ConfigurationException {
    }

    @Path("/access/rows/{rowId}/columns")
    @GET
    public Response getRowAccessibleColumns(@PathParam("rowId") String rowId, @QueryParam("table") String table, @QueryParam("username") String username) throws InvalidNodeTypeException, InvalidPropertyException, NodeNotFoundException, NoUserParameterException, ConfigurationException {
        System.out.println(rowId + " " + table + " " + username);
        //get the user id
        HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null);
        if(nodes.size() != 1) {
            throw new NodeNotFoundException(username);
        }
        Node userNode = nodes.iterator().next();

        //get the rowId
        nodes = nodeService.getNodes(table, String.valueOf(rowId), NodeType.OA.toString(), null);
        if(nodes.size() != 1) {
            throw new NodeNotFoundException(rowId);
        }
        Node rowNode = nodes.iterator().next();

        System.out.println(userNode.getId() + "&" + rowNode.getName() + "(" + rowNode.getId() + ")");

        //get the accessible children of the row with the given permission
        List<PmAccessEntry> accessibleChildren = analyticsService.getAccessibleChildren(rowNode.getId(), userNode.getId());
        for(PmAccessEntry entry : accessibleChildren) {
            System.out.println(entry.getTarget().getName() + ": " + entry.getOperations());
        }

        //for each column in the table, get the children, if a child is in the result of 1 add the column to a list
        HashSet<Node> columns = nodeService.getNodes(table, "Columns", NodeType.OA.toString(), null);
        if(nodes.size() != 1) {
            throw new NodeNotFoundException(table);
        }
        Node columnsNode = columns.iterator().next();

        //get the children of the columns node.  These are the columns of the table
        columns = nodeService.getChildrenOfType(columnsNode.getId(), NodeType.OA.toString());

        List<String> accesibleColumns = new ArrayList<>();
        for(Node column : columns) {
            HashSet<Node> children = nodeService.getChildrenOfType(column.getId(), NodeType.O.toString());
            for (PmAccessEntry entry : accessibleChildren) {
                Node target = entry.getTarget();
                if (children.contains(target) && entry.getOperations().contains(FILE_WRITE)) {
                    accesibleColumns.add(column.getName());
                }
            }
        }

        return new ApiResponse(accesibleColumns).toResponse();
    }

    @Path("/permissions")
    @GET
    public Response checkPermissions(@QueryParam("username") String username, @QueryParam("property") String property, @QueryParam("value") String value, @QueryParam("permission") String requiredPermission) throws PmException {
        HashSet<Node> nodes = nodeService.getNodes(null, username, NodeType.U.toString(), null);
        if(nodes.size() != 1) {
            throw new NodeNotFoundException(username);
        }
        Node userNode = nodes.iterator().next();

        //get the node(s) with the property (property=value)
        nodes = nodeService.getNodes(null, null, null, property, value);

        for(Node node : nodes) {
            PmAccessEntry userAccessOn = analyticsService.getUserPermissionsOn(node.getId(), userNode.getId());
            HashSet<String> operations = userAccessOn.getOperations();
            if(!operations.contains(requiredPermission)) {
                return new ApiResponse(false).toResponse();
            }
        }

        return new ApiResponse(true).toResponse();
    }

    @Path("nodes")
    @GET
    public Response getNodes(@QueryParam("namespace") String namespace,
                             @QueryParam("name") String name,
                             @QueryParam("type") String type,
                             @QueryParam("key") String key,
                             @QueryParam("value") String value) throws InvalidNodeTypeException, InvalidPropertyException {
        return new ApiResponse(nodeService.getNodes(namespace, name, type, key, value)).toResponse();
    }

    @Path("nodes/{baseId}")
    @POST
    public Response createNode(@PathParam("baseId") long baseId, CreateNodeRequest request) throws NullNameException, NodeNameExistsInNamespaceException, NullTypeException, InvalidPropertyException, DatabaseException, InvalidNodeTypeException, NodeNameExistsException, ConfigurationException, NodeIdExistsException, NodeNotFoundException, AssignmentExistsException, InvalidKeySpecException, NoSuchAlgorithmException {
        Node node = nodeService.createNode(request.getId(), request.getName(), request.getType(), request.getProperties());
        assignmentService.createAssignment(node.getId(), baseId);

        return new ApiResponse("success").toResponse();
    }

    @Path("nodes/{id}/children")
    @GET
    public Response getChildren(@PathParam("id") long id) throws NodeNotFoundException, InvalidNodeTypeException {
        return new ApiResponse(nodeService.getChildrenOfType(id, null)).toResponse();
    }

    @Path("assignments")
    @POST
    public Response createAsignment(AssignmentRequest request) throws NodeNotFoundException, AssignmentExistsException, ConfigurationException, DatabaseException {
        assignmentService.createAssignment(request.getChildId(), request.getParentId());
        return new ApiResponse("success").toResponse();
    }

    @Path("assignments")
    @DELETE
    public Response deleteAssignment(@QueryParam("childId") long childId,
                                     @QueryParam("parentId") long parentId) throws NodeNotFoundException, AssignmentDoesNotExistException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException {
        assignmentService.deleteAssignment(childId, parentId);
        return new ApiResponse(ApiResponse.DELETE_ASSIGNMENT_SUCCESS).toResponse();
    }

    @Path("nodes/{nodeId}")
    @DELETE
    public Response deleteNode(@PathParam("nodeId") long id)
            throws NodeNotFoundException, DatabaseException, ConfigurationException {
        nodeService.deleteNode(id);
        return new ApiResponse(ApiResponse.DELETE_NODE_SUCCESS).toResponse();
    }

}
