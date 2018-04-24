package gov.nist.policyserver.resources;


import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.access.PmAccessEntry;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.PermissionsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static gov.nist.policyserver.common.Constants.*;

@Path("/permissions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PermissionsResource {

    private PermissionsService permissionsService = new PermissionsService();

    public PermissionsResource() throws ConfigurationException {
    }

    @Path("/{targetId}")
    @GET
    public Response getPermissions(@PathParam("targetId") long targetId,
                                   @QueryParam("session") String session,
                                   @QueryParam("process") long process) throws NodeNotFoundException, SessionUserNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException {
        
        Node user = permissionsService.getSessionUser(session);

        return new ApiResponse(permissionsService.getUserPermissionsOn(targetId, user.getId())).toResponse();
    }

    @Path("/{targetId}/children")
    @GET
    public Response getAccessibleChildren(@PathParam("targetId") long targetId,
                                          @QueryParam("session") String session,
                                          @QueryParam("process") long process) throws NodeNotFoundException, NoUserParameterException, SessionUserNotFoundException {
        
        Node user = permissionsService.getSessionUser(session);
        
        return new ApiResponse(permissionsService.getAccessibleChildren(targetId, user.getId())).toResponse();
    }

    /*
    Not sure this method will ever be used
    @Path("/{targetId}/prohibitions")
    @GET
    public Response getProhibitedOps(@PathParam("targetId") long targetId,
                                     @QueryParam("subjectId") String subjectId,
                                     @QueryParam("subjectType") String subjectType,
                                     @QueryParam("session") String session,
                                     @QueryParam("process") long process) throws NodeNotFoundException, NoSubjectParameterException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, MissingPermissionException {
        if(subjectType.equals(ProhibitionSubjectType.U.toString())) {
            Node subjectUser;
            if(subjectId == null || subjectId.isEmpty()) {
                subjectUser = permissionsService.getSessionUser(session);
            } else {
                subjectUser = permissionsService.getSessionUser(subjectId);
            }

            return new ApiResponse(permissionsService.getProhibitedOps(targetId, subjectUser.getId(), subjectType)).toResponse();
        }

        return new ApiResponse(permissionsService.getProhibitedOps(targetId, Long.valueOf(subjectId), subjectType)).toResponse();
    }*/

    @Path("/sessions")
    @GET
    public Response getAccessibleNodes(@QueryParam("session") String session,
                                       @QueryParam("process") long process) throws NodeNotFoundException, NoUserParameterException, SessionUserNotFoundException, ConfigurationException {
        
        Node user = permissionsService.getSessionUser(session);


        return new ApiResponse(permissionsService.getAccessibleNodes(user.getId())).toResponse();
    }
}
