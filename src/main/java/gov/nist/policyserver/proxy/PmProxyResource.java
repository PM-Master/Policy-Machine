package gov.nist.policyserver.proxy;

import com.google.gson.Gson;
import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.evr.exceptions.InvalidEntityException;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.PmException;
import gov.nist.policyserver.response.ApiResponse;
import gov.nist.policyserver.translator.TranslateResponse;
import gov.nist.policyserver.translator.TranslatorService;
import gov.nist.policyserver.translator.exceptions.PolicyMachineException;
import net.sf.jsqlparser.JSQLParserException;
import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("proxy")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PmProxyResource {

    private String sqlId;
    private TranslatorService translatorService = new TranslatorService();

    public PmProxyResource() throws ConfigurationException, ClassNotFoundException {
        sqlId = "";
    }

    @POST
    public Response query(PmProxyRequest request) throws InvalidEntityException, PolicyMachineException, IOException, SQLException, PmException, JSQLParserException, ClassNotFoundException {
        String host = request.getHost();
        int port = request.getPort();
        String database = request.getDatabase();
        String dbUsername = request.getDbUsername();
        String dbPassword = request.getDbPassword();
        String sql = request.getSql();
        System.out.println("Original sql: " + sql);

        String username = "";
        String process = "";
        String[] pieces = sql.split("/\\*|\\*//*");
        if(pieces.length > 1) {
            String[] properties = pieces[1].split(",");
            for(String s : properties){
                if(s.startsWith("user=")){
                    username = s.split("=")[1];
                } else if(s.startsWith("process=")) {
                    process = s.split("=")[1];
                }
            }
        }

        //get jdbc ready to send to database
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, dbUsername, dbPassword);
        Statement stmt = connection.createStatement();
        stmt.execute("use " + database);
        stmt.close();

        stmt = connection.createStatement();

        String sqlId = "";
        if(username != null && !username.isEmpty()) {
            TranslateResponse translate = translatorService.translate(sql, username, process, host, port, dbUsername, dbPassword, database);
            sql = translate.getSql();
            sqlId = translate.getId();
        }

        System.out.println("Permitted sql: " + sql);

        boolean execute = stmt.execute(sql);
        if(execute) {
            //result set
            Gson gson = new Gson();
            List<HashMap<String, String>> results = new ArrayList<>();
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData metaData = rs.getMetaData();
            while(rs.next()) {
                HashMap<String, String> map = new HashMap<>();
                int columnCount = metaData.getColumnCount();
                for(int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    System.out.println("column: " + columnName);
                    if(columnName.contains(".")) {
                        columnName = columnName.split("\\.")[1];
                        System.out.println("changing to " + columnName);
                    }
                    Object object = rs.getObject(i);
                    String value = null;
                    if(object != null) {
                        value = String.valueOf(object);
                    }
                    map.put(columnName, value);
                }
                results.add(map);
            }
            String json = gson.toJson(results);

            DAO.getDao().getEvrManager().removeActiveSql(sqlId);

            return new ApiResponse(json).toResponse();
        } else {
            return new ApiResponse("Executed sql successfully").toResponse();
        }
    }
}
