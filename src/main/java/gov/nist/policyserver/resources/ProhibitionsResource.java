package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.CreateProhibitionRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.ProhibitionsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/prohibitions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProhibitionsResource {

    private ProhibitionsService prohibitionsService = new ProhibitionsService();

    public ProhibitionsResource() throws ConfigurationException {
    }

    @POST
    public Response createProhibition(CreateProhibitionRequest request)
            throws ProhibitionNameExistsException,
            DatabaseException, ConfigurationException, NullNameException {
        return new ApiResponse(prohibitionsService.createProhibition(request.getName(), request.getOperations(), request.isIntersection(), request.getResources(), request.getSubject())).toResponse();
    }

    @GET
    public Response getProhibitions(@QueryParam("subjectId") long subjectId, @QueryParam("resourceId") long resourceId) {
        return new ApiResponse(prohibitionsService.getProhibitions(subjectId, resourceId)).toResponse();
    }

    @Path("/{prohibitionName}")
    @GET
    public Response getProhibition(@PathParam("prohibitionName") String prohibitionName)
            throws ProhibitionDoesNotExistException {
        return new ApiResponse(prohibitionsService.getProhibition(prohibitionName)).toResponse();
    }

    @Path("/{prohibitionName}")
    @PUT
    public Response updateProhibition(@PathParam("prohibitionName") String prohibitionName,
                                      CreateProhibitionRequest request)
            throws DatabaseException, ProhibitionDoesNotExistException,
            NodeNotFoundException, InvalidProhibitionSubjectTypeException, ProhibitionResourceExistsException,
            ConfigurationException {
        return new ApiResponse(prohibitionsService.updateProhibition(request.getName(), request.isIntersection(), request.getOperations(), request.getResources(), request.getSubject())).toResponse();
    }

    @Path("/{prohibitionName}")
    @DELETE
    public Response deleteProhibition(@PathParam("prohibitionName") String prohibitionName)
            throws DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        prohibitionsService.deleteProhibition(prohibitionName) ;
        return new ApiResponse(ApiResponse.DELETE_PROHIBITION_SUCCESS).toResponse();
    }
}
