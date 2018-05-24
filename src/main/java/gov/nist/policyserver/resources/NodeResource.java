package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.requests.CreateNodeRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AnalyticsService;
import gov.nist.policyserver.service.NodeService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.common.Constants.*;

@Path("/nodes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {

    private NodeService      nodeService      = new NodeService();
    private AnalyticsService analyticsService = new AnalyticsService();

    public NodeResource() throws ConfigurationException {
    }

    @GET
    public Response getNodes(@QueryParam("namespace") String namespace,
                             @QueryParam("name") String name,
                             @QueryParam("type") String type,
                             @QueryParam("key") String key,
                             @QueryParam("value") String value,
                             @QueryParam("session") String session,
                             @QueryParam("process") long process)
            throws InvalidNodeTypeException, InvalidPropertyException,
            SessionUserNotFoundException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //get the nodes that are accessible to the user
        List<PmAccessEntry> accessibleNodes = analyticsService.getAccessibleNodes(user);
        HashSet<Node> nodes = new HashSet<>();
        for(PmAccessEntry entry : accessibleNodes) {
            nodes.add(entry.getTarget());
        }

        return new ApiResponse(nodeService.getNodes(nodes, namespace, name, type, key, value)).toResponse();
    }

    @POST
    public Response createNode(CreateNodeRequest request,
                               @QueryParam("session") String session,
                               @QueryParam("process") long process)
            throws NullNameException, NodeNameExistsInNamespaceException, NullTypeException,
            InvalidPropertyException, DatabaseException, InvalidNodeTypeException,
            NodeNameExistsException, ConfigurationException, NodeIdExistsException,
            NodeNotFoundException, SessionUserNotFoundException, NoSubjectParameterException,
            MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //get connector node
        Node connector = analyticsService.getConnector();

        //check user can create in connector
        analyticsService.checkPermissions(user, process, connector.getId(), CREATE_NODE);


        return new ApiResponse(nodeService.createNode(request.getId(), request.getName(), request.getType(), request.getProperties())).toResponse();
    }

    @Path("/{nodeId}")
    @GET
    public Response getNode(@PathParam("nodeId") long id,
                            @QueryParam("session") String session,
                            @QueryParam("process") long process) throws NodeNotFoundException, SessionUserNotFoundException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //check user can access the node
        analyticsService.checkPermissions(user, process, id, ANY_OPERATIONS);

        return new ApiResponse(nodeService.getNode(id)).toResponse();
    }

    @Path("/{nodeId}")
    @PUT
    public Response updateNode(@PathParam("nodeId") long id,
                               CreateNodeRequest request,
                               @QueryParam("session") String session,
                               @QueryParam("process") long process)
            throws NodeNotFoundException, DatabaseException, ConfigurationException,
            SessionUserNotFoundException, NoSubjectParameterException, MissingPermissionException,
            InvalidProhibitionSubjectTypeException, InvalidPropertyException, PropertyNotFoundException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //check user can update the node
        analyticsService.checkPermissions(user, process, id, UPDATE_NODE);

        return new ApiResponse(nodeService.updateNode(id, request.getName(), request.getProperties())).toResponse();
    }

    @Path("/{nodeId}")
    @DELETE
    public Response deleteNode(@PathParam("nodeId") long id,
                               @QueryParam("session") String session,
                               @QueryParam("process") long process)
            throws NodeNotFoundException, DatabaseException, ConfigurationException,
            SessionUserNotFoundException, NoSubjectParameterException,
            MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //check user can delete the node
        analyticsService.checkPermissions(user, process, id, DELETE_NODE);

        nodeService.deleteNode(id);
        return new ApiResponse(ApiResponse.DELETE_NODE_SUCCESS).toResponse();
    }

    @Path("/{nodeId}/properties/{key}")
    @DELETE
    public Response deleteNodeProperty(@PathParam("nodeId") long id,
                                       @PathParam("key") String key,
                                       @QueryParam("session") String session,
                                       @QueryParam("process") long process)
            throws DatabaseException, NodeNotFoundException, PropertyNotFoundException, ConfigurationException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //check user can delete the node
        analyticsService.checkPermissions(user, process, id, UPDATE_NODE);

        nodeService.deleteNodeProperty(id, key);
        return new ApiResponse(ApiResponse.DELETE_NODE_PROPERTY_SUCCESS).toResponse();
    }

    @Path("{nodeId}/children")
    @GET
    public Response getNodeChildren(@PathParam("nodeId") long id,
                                    @QueryParam("type") String type,
                                    @QueryParam("session") String session,
                                    @QueryParam("process") long process)
            throws NodeNotFoundException, SessionUserNotFoundException, NoUserParameterException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        List<PmAccessEntry> accessibleChildren = analyticsService.getAccessibleChildren(id, user.getId());

        HashSet<Node> nodes = new HashSet<>();
        for(PmAccessEntry entry : accessibleChildren) {
            if(type == null || type.equals(entry.getTarget().getType().toString())) {
                nodes.add(entry.getTarget());
            }
        }

        return new ApiResponse(nodes).toResponse();
    }

    @Path("/{nodeId}/children")
    @DELETE
    public Response deleteNodeChildren(@PathParam("nodeId") long id,
                                       @QueryParam("type") String type,
                                       @QueryParam("session") String session,
                                       @QueryParam("process") long process)
            throws InvalidNodeTypeException, NodeNotFoundException, DatabaseException,
            ConfigurationException, SessionUserNotFoundException, NoSubjectParameterException,
            InvalidProhibitionSubjectTypeException, MissingPermissionException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        HashSet<Node> children = nodeService.getChildrenOfType(id, type);

        for(Node node : children) {
            PmAccessEntry perms = analyticsService.getUserPermissionsOn(node.getId(), user.getId());

            //check the user can delete the node
            if(!perms.getOperations().contains(DELETE_NODE)) {
                throw new MissingPermissionException("Can not delete child of " + id + " with id " + perms.getTarget().getId());
            }
        }

        nodeService.deleteNodeChildren(id, type);
        return new ApiResponse(ApiResponse.DELETE_NODE_CHILDREN_SUCESS).toResponse();
    }

    @Path("/{nodeId}/parents")
    @GET
    public Response getNodeParents(@PathParam("nodeId") long id,
                                   @QueryParam("type") String type,
                                   @QueryParam("session") String session,
                                   @QueryParam("process") long process)
            throws InvalidNodeTypeException, NodeNotFoundException, SessionUserNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException, MissingPermissionException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        HashSet<Node> parents = nodeService.getParentsOfType(id, type);
        for (Node node : parents) {
            PmAccessEntry perms = analyticsService.getUserPermissionsOn(node.getId(), user.getId());
            if(perms.getOperations().isEmpty()) {
                throw new MissingPermissionException("Can not access parent of " + id);
            }
        }

        return new ApiResponse(nodeService.getParentsOfType(id, type)).toResponse();
    }
}
