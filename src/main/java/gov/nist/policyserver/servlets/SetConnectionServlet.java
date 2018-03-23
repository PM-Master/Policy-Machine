package gov.nist.policyserver.servlets;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.DatabaseException;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

public class SetConnectionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        String database = request.getParameter("database");
        String host = request.getParameter("host");
        String port = request.getParameter("port");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String schema = request.getParameter("schema");

        Properties props = new Properties();
        props.put("database", database);
        props.put("host", host);
        props.put("port", String.valueOf(port));
        props.put("username", username);
        props.put("password", password);
        props.put("schema", schema == null ? "" : schema);

        try {
            DAO.init(props);

            request.getRequestDispatcher("/config.jsp?display=block&result=success&message=Database+connection+successful").forward(request, response);
        }
        catch (DatabaseException | ConfigurationException e) {
            request.getRequestDispatcher("/config.jsp?display=block&result=danger&message=" + e.getMessage().replaceAll(" ", "+")).forward(request, response);
        }
    }
}