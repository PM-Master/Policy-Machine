package gov.nist.policyserver.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.AddNodePropertyRequest;
import gov.nist.policyserver.requests.CreateNodeRequest;
import gov.nist.policyserver.requests.UpdateNodeRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.NodeService;

@Path("/nodes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {

    private NodeService nodeService = new NodeService();

    public NodeResource() throws ConfigurationException {
    }

    @GET
    public Response getNodes(@QueryParam("namespace") String namespace,
                             @QueryParam("name") String name,
                             @QueryParam("type") String type,
                             @QueryParam("key") String key,
                             @QueryParam("value") String value) throws InvalidNodeTypeException, InvalidPropertyException {
        return new ApiResponse(nodeService.getNodes(namespace, name, type, key, value)).toResponse();
    }

    @POST
    public Response createNode(CreateNodeRequest request) throws NullNameException, NodeNameExistsInNamespaceException, NameInNamespaceNotFoundException, NullTypeException, InvalidPropertyException, DatabaseException, InvalidNodeTypeException, NodeNameExistsException, ConfigurationException {
        return new ApiResponse(nodeService.createNode(request.getName(), request.getType(), request.getDescription(), request.getProperties())).toResponse();
    }


    @Path("/{namespace}/{nodeName}")
    @GET
    public Response getNodeInNamespace(@PathParam("namespace") String namespace, @PathParam("nodeName") String nodeName) throws NameInNamespaceNotFoundException, InvalidNodeTypeException, InvalidPropertyException {
        return new ApiResponse(nodeService.getNodeInNamespace(namespace, nodeName)).toResponse();
    }

    @Path("/{namespace}/{nodeName}")
    @DELETE
    public Response deleteNodeInNamespace(@PathParam("namespace") String namespace, @PathParam("nodeName") String nodeName) throws InvalidNodeTypeException, DatabaseException, NodeNotFoundException, NameInNamespaceNotFoundException, InvalidPropertyException, ConfigurationException {
        nodeService.deleteNodeInNamespace(namespace, nodeName);
        return new ApiResponse(ApiResponse.DELETE_NODE_IN_NAMESPACE_SUCCESS).toResponse();
    }

    @Path("/{nodeId}")
    @GET
    public Response getNode(@PathParam("nodeId") long id) throws NodeNotFoundException {
        return new ApiResponse(nodeService.getNode(id)).toResponse();
    }

    @Path("/{nodeId}")
    @PUT
    public Response updateNode(@PathParam("nodeId") long id, UpdateNodeRequest request) throws NodeNotFoundException, DatabaseException, ConfigurationException {
        return new ApiResponse(nodeService.updateNode(id, request.getName(), request.getDescription())).toResponse();
    }

    @Path("/{nodeId}")
    @DELETE
    public Response deleteNode(@PathParam("nodeId") long id)
            throws NodeNotFoundException, DatabaseException, ConfigurationException {
        nodeService.deleteNode(id);
        return new ApiResponse(ApiResponse.DELETE_NODE_SUCCESS).toResponse();
    }

    @Path("/{nodeId}/properties")
    @POST
    public Response addNodeProperty(@PathParam("nodeId") long id, AddNodePropertyRequest request) throws NodeNotFoundException, InvalidPropertyException, DatabaseException, ConfigurationException {
        return new ApiResponse(nodeService.addNodeProperty(id, request.getKey(), request.getValue())).toResponse();
    }

    @Path("/{nodeId}/properties")
    @GET
    public Response getNodeProperties(@PathParam("nodeId") long id) throws NodeNotFoundException {
        return new ApiResponse(nodeService.getNodeProperties(id)).toResponse();
    }

    @Path("/{nodeId}/properties/{key}")
    @GET
    public Response getNodeProperty(@PathParam("nodeId") long id, @PathParam("key") String key) throws NodeNotFoundException, PropertyNotFoundException {
        return new ApiResponse(nodeService.getNodeProperty(id, key)).toResponse();
    }

    @Path("/{nodeId}/properties/{key}")
    @DELETE
    public Response deleteNodeProperty(@PathParam("nodeId") long id, @PathParam("key") String key) throws DatabaseException, NodeNotFoundException, PropertyNotFoundException, ConfigurationException {
        nodeService.deleteNodeProperty(id, key);
        return new ApiResponse(ApiResponse.DELETE_NODE_PROPERTY_SUCCESS).toResponse();
    }

    @Path("{nodeId}/children")
    @GET
    public Response getNodeChildren(@PathParam("nodeId") long id, @QueryParam("type") String type) throws NodeNotFoundException, InvalidNodeTypeException {
        return new ApiResponse(nodeService.getChildrenOfType(id, type)).toResponse();
    }

    @Path("/{nodeId}/children")
    @DELETE
    public Response deleteNodeChildren(@PathParam("nodeId") long id, @QueryParam("type") String type) throws InvalidNodeTypeException, NodeNotFoundException, DatabaseException, ConfigurationException {
        nodeService.deleteNodeChildren(id, type);
        return new ApiResponse(ApiResponse.DELETE_NODE_CHILDREN_SUCESS).toResponse();
    }

    @Path("/{nodeId}/parents")
    @GET
    public Response getNodeParents(@PathParam("nodeId") long id, @QueryParam("type") String type) throws InvalidNodeTypeException, NodeNotFoundException {
        return new ApiResponse(nodeService.getParentsOfType(id, type)).toResponse();
    }
}
