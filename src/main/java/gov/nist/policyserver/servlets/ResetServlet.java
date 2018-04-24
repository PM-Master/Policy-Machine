package gov.nist.policyserver.servlets;

import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.service.ConfigurationService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
public class ResetServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        try {
            ConfigurationService service = new ConfigurationService();
            service.reset();

            request.getRequestDispatcher("/config.jsp?display=block&result=success&message=Data+reset+successfully").forward(request, response);
        }
        catch (ConfigurationException | DatabaseException e) {
            request.getRequestDispatcher("/config.jsp?display=block&result=danger&message=" + e.getMessage().replaceAll(" ", "+")).forward(request, response);
        }
    }
}