package gov.nist.policyserver.translator;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.translator.exceptions.PMAccessDeniedException;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("translate")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TranslatorResource {
    private TranslatorService translatorService = new TranslatorService();

    public TranslatorResource() throws ConfigurationException {
    }

    @POST
    public Response translate(TranslateRequest request) throws ClassNotFoundException, SQLException, JSQLParserException, IOException, NodeNotFoundException, PMAccessDeniedException, PolicyMachineException, InvalidNodeTypeException, NameInNamespaceNotFoundException, InvalidPropertyException, NoUserParameterException {
        return new ApiResponse(translatorService.translate(request.getSql(), request.getUsername(),
                request.getHost(), request.getPort(), request.getDbUsername(),
                request.getDbPassword(), request.getDatabase())).toResponse();
    }

}
