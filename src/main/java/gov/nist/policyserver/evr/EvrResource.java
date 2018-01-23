package gov.nist.policyserver.evr;


import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.response.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("createEvr")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EvrResource {
    private EvrService evrService = new EvrService();

    public EvrResource() throws ConfigurationException {
    }

    @POST
    public Response createEvr(EvrRequest request) throws IOException {
        return new ApiResponse(evrService.createEvr(request.getScriptName(), request.getSource())).toResponse();
    }

    @Path("/{scriptName}")
    @PUT
    public Response updateEvr(EvrRequest request, @PathParam("scriptName") String scriptName) {
        return new ApiResponse(evrService.update(request.getScriptName(), request.getSource())).toResponse();
    }

    @Path("/{scriptName}")
    @POST
    public Response enableEvr(@PathParam("scriptName") String scriptName) {
        evrService.enableEvr(scriptName);
        return new ApiResponse("Script " + scriptName + " was successfully enabled").toResponse();
    }

}
