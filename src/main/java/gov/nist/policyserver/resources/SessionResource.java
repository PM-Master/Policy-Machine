package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.CreateSessionRequest;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.SessionService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sessions")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SessionResource {
    private SessionService sessionService = new SessionService();

    public SessionResource() throws ConfigurationException {
    }

    @POST
    public Response createSession(CreateSessionRequest request)
            throws NullNameException, NodeNameExistsInNamespaceException, NodeNameExistsException,
            NodeNotFoundException, DatabaseException, InvalidNodeTypeException,
            InvalidPropertyException, ConfigurationException, NullTypeException, NodeIdExistsException {
        String username = request.getUsername();
        String password = request.getPassword();

        return new ApiResponse(sessionService.createSession(username, password)).toResponse();
    }

    @Path("/{sessionId}/access")
    @GET
    public Response getSessionAccess(@PathParam("sessionId") String sessionId) throws SessionDoesNotExistException, NodeNotFoundException, NoUserParameterException, PropertyNotFoundException, InvalidPropertyException, InvalidNodeTypeException {
        return new ApiResponse(sessionService.getSessionAccess(sessionId)).toResponse();
    }
}
