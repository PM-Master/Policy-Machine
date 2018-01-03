package gov.nist.policyserver.resources;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.requests.*;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.service.ConfigurationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static gov.nist.policyserver.common.Constants.SUCCESS;

@Path("/configuration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource {
    private ConfigurationService configService = new ConfigurationService();

    public ConfigurationResource() throws ConfigurationException {
    }

    @GET
    public Response getConfiguration() {
        return new ApiResponse(configService.save()).toResponse();
    }

    @Path("connection")
    @POST
    public Response connect(ConnectRequest request) throws DatabaseException, ConfigurationException {
        String database = request.getDatabase();
        String host = request.getHost();
        int port = request.getPort();
        String schema = request.getSchema();
        String username = request.getUsername();
        String password = request.getPassword();

        configService.connect(database, host, port, schema, username, password);

        return new ApiResponse(SUCCESS).toResponse();
    }

    @Path("interval")
    @POST
    public Response setInterval(SetIntervalRequest request) throws ConfigurationException {
        int interval = request.getInterval();

        configService.setInterval(interval);
        return new ApiResponse(interval).toResponse();
    }

    @Path("data")
    @POST
    public Response importData(ConnectRequest request) throws DatabaseException, NullNameException, ConfigurationException, NullTypeException, NodeNameExistsException, NodeNameExistsInNamespaceException, InvalidNodeTypeException, InvalidPropertyException, AssignmentExistsException, PropertyNotFoundException, NodeNotFoundException, NameInNamespaceNotFoundException {
        String host = request.getHost();
        int port = request.getPort();
        String schema = request.getSchema();
        String username = request.getUsername();
        String password = request.getPassword();

        configService.importData(host, port, schema, username, password);

        return new ApiResponse(SUCCESS).toResponse();
    }

    @Path("data/tables")
    @POST
    public Response getData(DataRequest request) throws PmException {
        String host = request.getHost();
        int port = request.getPort();
        String username = request.getUsername();
        String password = request.getPassword();
        String schema = request.getSchema();
        String tableName = request.getTable();

        return new ApiResponse(configService.getData(host, port, username, password, schema, tableName)).toResponse();
    }

    @Path("graph")
    @GET
    public Response getGraph() throws ConfigurationException, DatabaseException, NodeNotFoundException, InvalidNodeTypeException, InvalidPropertyException {
        return new ApiResponse(configService.getGraph()).toResponse();
    }
}
