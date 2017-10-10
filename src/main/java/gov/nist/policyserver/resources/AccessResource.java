package gov.nist.policyserver.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.ProhibitedOpsRequest;
import gov.nist.policyserver.requests.AssociationRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AccessService;


@Path("/access")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AccessResource {

    private AccessService accessService = new AccessService();

    public AccessResource() throws ConfigurationException {
    }

    @Path("/resources")
    @POST
    public Response grantAccess(AssociationRequest request)
            throws NodeNotFoundException, DatabaseException, ConfigurationException {
        accessService.grantAccess(request.getUaId(), request.getTargetId(), request.getOps(), request.isInherit());
        return new ApiResponse(ApiResponse.GRANT_ACCESS_SUCCESS).toResponse();
    }

    @Path("/resources/{targetId}")
    @GET
    public Response getAccess(@PathParam("targetId") long targetId,
                              @QueryParam("userId") long userId)
            throws PmException {
        if(userId <= 0){
            return new ApiResponse(accessService.getUsersWithAccessOn(targetId)).toResponse();
        }
        return new ApiResponse(accessService.getUserAccessOn(targetId, userId)).toResponse();
    }

    @Path("/resources/{targetId}")
    @PUT
    public Response updateAccess(@PathParam("targetId") long targetId,
                                 @QueryParam("add") @DefaultValue("true") boolean add,
                                 AssociationRequest request)
            throws DatabaseException, NodeNotFoundException, AssociationDoesNotExistException, ConfigurationException {
        accessService.updateAccess(targetId, request.getUaId(), add, request.getOps(), request.isInherit());
        return new ApiResponse(ApiResponse.UPDATE_ACCESS_SUCESS).toResponse();
    }

    @Path("/resources/{targetId}")
    @DELETE
    public Response removeAccess(@PathParam("targetId") long targetId,
                                 @QueryParam("uaId") long uaId)
            throws NodeNotFoundException, NoUserParameterException, AssociationDoesNotExistException, DatabaseException, ConfigurationException {
        accessService.removeAccess(targetId, uaId);
        return new ApiResponse(ApiResponse.REMOVE_ACCESS_SUCCESS).toResponse();
    }

    @Path("/resources/{targetId}/children")
    @GET
    public Response getAccessibleChildren(@PathParam("targetId") long targetId,
                                          @QueryParam("userId") long userId)
            throws NodeNotFoundException, NoUserParameterException {
        return new ApiResponse(accessService.getAccessibleChildren(targetId, userId)).toResponse();
    }

    @Path("/resources/{targetId}/prohibitions")
    @POST
    public Response getProhibitedOps(@PathParam("targetId") long targetId, ProhibitedOpsRequest request)
            throws NodeNotFoundException, NoUserParameterException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException {
        return new ApiResponse(accessService.getProhibitedOps(targetId, request.getSubjectId(), request.getSubjectType())).toResponse();
    }

    @Path("/users/{userId}/resources")
    @GET
    public Response getAccessibleResources(@PathParam("userId") long userId)
            throws NodeNotFoundException, NoUserParameterException {
        return new ApiResponse(accessService.getAccessibleNodes(userId)).toResponse();
    }
}

