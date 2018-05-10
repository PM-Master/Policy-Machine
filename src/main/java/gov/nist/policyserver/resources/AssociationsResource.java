package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.requests.AssociationRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AssociationsService;
import gov.nist.policyserver.service.PermissionsService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static gov.nist.policyserver.common.Constants.*;

@Path("/associations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssociationsResource {

    private AssociationsService associationsService = new AssociationsService();
    private PermissionsService  permissionsService  = new PermissionsService();

    public AssociationsResource() throws ConfigurationException {
    }

    @POST
    public Response createAssociation(AssociationRequest request,
                                      @QueryParam("session") String session,
                                      @QueryParam("process") long process)
            throws NodeNotFoundException, AssociationExistsException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = permissionsService.getSessionUser(session);

        //check user can create an association for the target and the subject
        //1. can create association for the target
        permissionsService.checkPermissions(user, process, request.getTargetId(), CREATE_ASSOCIATION);

        //TODO associations on UAs not yet implemented
        //2. can create an association for the subject
        //permissionsService.checkPermissions(user, process, request.getUaId(), ASSIGN);

        associationsService.createAssociation(request.getUaId(), request.getTargetId(), request.getOps(), request.isInherit());
        return new ApiResponse(ApiResponse.CREATE_ASSOCIATION_SUCCESS).toResponse();
    }

    @GET
    public Response getAssociations(@QueryParam("targetId") long targetId,
                                    @QueryParam("session") String session,
                                    @QueryParam("process") long process) throws NodeNotFoundException, InvalidPropertyException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = permissionsService.getSessionUser(session);

        //get the connector node
        Node connector = permissionsService.getConnector();

        //check user can get associations
        permissionsService.checkPermissions(user, process, connector.getId(), GET_ASSOCIATIONS);

        if(targetId != 0) {
            return new ApiResponse(associationsService.getTargetAssociations(targetId)).toResponse();
        } else {
            return new ApiResponse(associationsService.getAssociations()).toResponse();
        }
    }

    @Path("/{targetId}")
    @PUT
    public Response updateAssociation(@PathParam("targetId") long targetId,
                                      AssociationRequest request,
                                      @QueryParam("session") String session,
                                      @QueryParam("process") long process)
            throws NodeNotFoundException, AssociationDoesNotExistException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = permissionsService.getSessionUser(session);

        //check user can create an association for the target and the subject
        //1. can update association for the target
        permissionsService.checkPermissions(user, process, request.getTargetId(), UPDATE_ASSOCIATION);

        //TODO associations on UAs not yet implemented
        //2. can update an association for the subject
        //permissionsService.checkPermissions(user, process, request.getUaId(), UPDATE_ASSOCIATION);

        associationsService.updateAssociation(targetId, request.getUaId(), request.getOps(), request.isInherit());
        return new ApiResponse(ApiResponse.UPDATE_ASSOCIATION_SUCCESS).toResponse();
    }

    @Path("/{targetId}/subjects/{subjectId}")
    @DELETE
    public Response deleteAssociation(@PathParam("targetId") long targetId,
                                      @PathParam("subjectId") long uaId,
                                      @QueryParam("session") String session,
                                      @QueryParam("process") long process) throws NodeNotFoundException, NoUserParameterException, AssociationDoesNotExistException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = permissionsService.getSessionUser(session);

        //check user can create an association for the target and the subject
        //1. can delete association for the target
        permissionsService.checkPermissions(user, process, targetId, DELETE_ASSOCIATION);

        //TODO associations on UAs not yet implemented
        //2. can delete an association for the subject
        //permissionsService.checkPermissions(user, process, uaId, DELETE_ASSOCIATION);

        associationsService.deleteAssociation(targetId, uaId);
        return new ApiResponse(ApiResponse.DELETE_ASSOCIATION_ASSOCIATION).toResponse();
    }


    @Path("/subjects/{subjectId}")
    @GET
    public Response getSubjectAssociations(@PathParam("subjectId") long subjectId,
                                           @QueryParam("session") String session,
                                           @QueryParam("process") long process)
            throws NodeNotFoundException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, ConfigurationException, SessionDoesNotExistException {
        //PERMISSION CHECK
        //get user from username
        Node user = permissionsService.getSessionUser(session);

        //check user can get associations that the subject is in
        permissionsService.checkPermissions(user, process, subjectId, GET_ASSOCIATIONS);

        return new ApiResponse(associationsService.getSubjectAssociations(subjectId)).toResponse();
    }
}
