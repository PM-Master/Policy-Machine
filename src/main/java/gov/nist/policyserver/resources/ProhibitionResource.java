package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.AddOperationsToProhibitionRequest;
import gov.nist.policyserver.requests.AddResourceToProhibitionRequest;
import gov.nist.policyserver.requests.PostSubjectToProhibitionRequest;
import gov.nist.policyserver.requests.CreateProhibitionRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.ProhibitionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/prohibitions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProhibitionResource {

    private ProhibitionService prohibitionService = new ProhibitionService();

    public ProhibitionResource() throws ConfigurationException {
    }

    @POST
    public Response createProhibition(CreateProhibitionRequest request)
            throws InvalidProhibitionSubjectTypeException, ProhibitionNameExistsException, ProhibitionDoesNotExistException, NodeNotFoundException, DatabaseException, ProhibitionResourceExistsException, ConfigurationException {
        return new ApiResponse(prohibitionService.createProhibition(request.getName(), request.getOperations(), request.isIntersection(), request.getResources(), request.getSubject())).toResponse();
    }

    @Path("/{prohibitionName}")
    @GET
    public Response getProhibition(@PathParam("prohibitionName") String prohibitionName) throws ProhibitionDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibition(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}")
    @DELETE
    public Response deleteProhibition(@PathParam("prohibitionName") String prohibitionName) throws DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        prohibitionService.deleteProhibition(prohibitionName) ;
        return new ApiResponse(ApiResponse.DELETE_PROHIBITION_SUCCESS).toResponse();
    }

    @Path("/{prohibitionName}/resources")
    @POST
    public Response addResourceToProhibition(@PathParam("prohibitionName") String prohibitionName, AddResourceToProhibitionRequest request) throws NodeNotFoundException, DatabaseException, ProhibitionDoesNotExistException, ProhibitionResourceExistsException, ConfigurationException {
        return new ApiResponse(prohibitionService.addResourceToProhibition(prohibitionName, request.getResourceId(), request.isCompliment())).toResponse();
    }

    @Path("/{prohibitionName}/resources")
    @GET
    public Response getProhibitionResources(@PathParam("prohibitionName") String prohibitionName) throws ProhibitionDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibitionResources(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}/resources/{resourceId}")
    @GET
    public Response getProhibitionResource(@PathParam("prohibitionName") String prohibitionName, @PathParam("resourceId") long resourceId) throws ProhibitionDoesNotExistException, ProhibitionResourceDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibitionResource(prohibitionName, resourceId)).toResponse();
    }

    @Path("/{prohibitionName}/resources/{resourceId}")
    @DELETE
    public Response deleteProhibitionResource(@PathParam("prohibitionName") String prohibitionName, @PathParam("resourceId") long resourceId) throws DatabaseException, ProhibitionDoesNotExistException, ProhibitionResourceDoesNotExistException, ConfigurationException {
        return new ApiResponse(prohibitionService.deleteProhibitionResource(prohibitionName, resourceId)).toResponse();
    }

    @Path("/{prohibitionName}/subject")
    @PUT
    public Response setProhibitionSubject(@PathParam("prohibitionName") String prohibitionName, PostSubjectToProhibitionRequest request) throws DatabaseException, InvalidProhibitionSubjectTypeException, ProhibitionDoesNotExistException, ConfigurationException {
        return new ApiResponse(prohibitionService.setProhibitionSubject(prohibitionName, request.getSubjectId(), request.getSubjectType())).toResponse();
    }

    @Path("/{prohibitionName}/subject")
    @GET
    public Response getProhibitionSubject(@PathParam("prohibitionName") String prohibitionName) throws ProhibitionDoesNotExistException, ProhibitionSubjectDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibitionSubject(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}/operations")
    @POST
    public Response addOperationsToProhibition(@PathParam("prohibitionName") String prohibitionName, AddOperationsToProhibitionRequest request) throws DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        return new ApiResponse(prohibitionService.addOperationsToProhibition(prohibitionName, request.getOperations())).toResponse();
    }

    @Path("/{prohibitionName}/operations")
    @GET
    public Response getProhibitionOperations(@PathParam("prohibitionName") String prohibitionName) throws ProhibitionDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibitionOperations(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}/operations/{op}")
    @DELETE
    public Response removeOperationFromProhibition(@PathParam("prohibitionName") String prohibitionName, @PathParam("op") String op) throws DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        return new ApiResponse(prohibitionService.removeOperationFromProhibition(prohibitionName, op)).toResponse();
    }
}
