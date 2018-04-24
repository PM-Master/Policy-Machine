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
            throws InvalidProhibitionSubjectTypeException, ProhibitionNameExistsException, ProhibitionDoesNotExistException,
            NodeNotFoundException, DatabaseException, ProhibitionResourceExistsException, ConfigurationException {
        return new ApiResponse(prohibitionService.createProhibition(request.getName(), request.getOperations(), request.isIntersection(), request.getResources(), request.getSubject())).toResponse();
    }

    @GET
    public Response getProhibitions(@QueryParam("subjectId") long subjectId, @QueryParam("resourceId") long resourceId) {
        return new ApiResponse(prohibitionService.getProhibitions(subjectId, resourceId)).toResponse();
    }

    @Path("/{prohibitionName}")
    @GET
    public Response getProhibition(@PathParam("prohibitionName") String prohibitionName)
            throws ProhibitionDoesNotExistException {
        return new ApiResponse(prohibitionService.getProhibition(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}")
    @PUT
    public Response updateProhibition(@PathParam("prohibitionName") String prohibitionName,
                                      CreateProhibitionRequest request)
            throws DatabaseException, ProhibitionDoesNotExistException, ProhibitionResourceDoesNotExistException,
            NodeNotFoundException, InvalidProhibitionSubjectTypeException, ProhibitionResourceExistsException,
            ConfigurationException {
        return new ApiResponse(prohibitionService.updateProhibition(request.getName(), request.getOperations(), request.getResources(), request.getSubject())).toResponse();
    }

    @Path("/{prohibitionName}")
    @DELETE
    public Response deleteProhibition(@PathParam("prohibitionName") String prohibitionName)
            throws DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        prohibitionService.deleteProhibition(prohibitionName) ;
        return new ApiResponse(ApiResponse.DELETE_PROHIBITION_SUCCESS).toResponse();
    }
}
