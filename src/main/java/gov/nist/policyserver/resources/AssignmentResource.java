package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.requests.AssignmentRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AnalyticsService;
import gov.nist.policyserver.service.AssignmentService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static gov.nist.policyserver.common.Constants.*;

@Path("/assignments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignmentResource {

    private AssignmentService assignmentService = new AssignmentService();
    private AnalyticsService  analyticsService  = new AnalyticsService();

    public AssignmentResource() throws ConfigurationException {
    }

    @GET
    public Response isAssigned(@QueryParam("childId") long childId,
                               @QueryParam("parentId") long parentId,
                               @QueryParam("session") String session,
                               @QueryParam("process") long process) throws NodeNotFoundException {
        return new ApiResponse(assignmentService.isAssigned(childId, parentId)).toResponse();
    }

    @POST
    public Response createAssignment(AssignmentRequest request,
                                     @QueryParam("session") String session,
                                     @QueryParam("process") long process)
            throws NodeNotFoundException, AssignmentExistsException, NoSubjectParameterException,
            MissingPermissionException, InvalidProhibitionSubjectTypeException, DatabaseException,
            ConfigurationException, SessionUserNotFoundException, SessionDoesNotExistException {
        Node user = analyticsService.getSessionUser(session);

        //check user can assign the child node to the parent node
        //1. can assign (type) TO parent node
        analyticsService.checkPermissions(user, process, request.getParentId(), ASSIGN_TO);

        //2. can assign child
        analyticsService.checkPermissions(user, process, request.getChildId(), ASSIGN);

        assignmentService.createAssignment(request.getChildId(), request.getParentId());

        return new ApiResponse(ApiResponse.CREATE_ASSIGNMENT_SUCCESS).toResponse();
    }

    @DELETE
    public Response deleteAssignment(@QueryParam("childId") long childId,
                                     @QueryParam("parentId") long parentId,
                                     @QueryParam("session") String session,
                                     @QueryParam("process") long process) throws NodeNotFoundException, AssignmentDoesNotExistException, ConfigurationException, DatabaseException, NoSubjectParameterException, MissingPermissionException, InvalidProhibitionSubjectTypeException, SessionUserNotFoundException, SessionDoesNotExistException {
        //get user from username
        Node user = analyticsService.getSessionUser(session);

        //PERMISSION CHECK
        //check user can deassign the child node from the parent node
        //1. can assign (type) TO parent node
        analyticsService.checkPermissions(user, process, parentId, DEASSIGN_FROM);

        //2. can deassign child
        analyticsService.checkPermissions(user, process, childId, DEASSIGN);

        assignmentService.deleteAssignment(childId, parentId);

        return new ApiResponse(ApiResponse.DELETE_ASSIGNMENT_SUCCESS).toResponse();
    }
}
