package gov.nist.policyserver.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.AssignmentRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.AssignmentService;

@Path("/assignments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignmentResource {

    private AssignmentService assignmentService = new AssignmentService();

    public AssignmentResource() throws ConfigurationException {
    }

    @GET
    public Response isAssigned(@QueryParam("childId") long childId,
                               @QueryParam("parentId") long parentId) throws NodeNotFoundException, CustomException {
        return new ApiResponse(assignmentService.isAssigned(childId, parentId)).toResponse();
    }

    @POST
    public Response createAssignment(AssignmentRequest request) throws DatabaseException, NodeNotFoundException, ConfigurationException, AssignmentExistsException {
        assignmentService.createAssignment(request.getChildId(), request.getParentId());
        return new ApiResponse(ApiResponse.CREATE_ASSIGNMENT_SUCCESS).toResponse();
    }

    @DELETE
    public Response deleteAssignment(@QueryParam("childId") long childId,
                                     @QueryParam("parentId") long parentId) throws NodeNotFoundException, AssignmentDoesNotExistException, ConfigurationException, DatabaseException {
        assignmentService.deleteAssignment(childId, parentId);
        return new ApiResponse(ApiResponse.DELETE_ASSIGNMENT_SUCCESS).toResponse();
    }
}
