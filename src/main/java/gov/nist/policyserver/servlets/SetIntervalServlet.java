package gov.nist.policyserver.servlets;

import gov.nist.policyserver.dao.DAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class SetIntervalServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        String inter = request.getParameter("interval");
        if(inter != null){
            try {
                DAO.setInterval(Integer.parseInt(inter));

                request.setAttribute("successMessage", "Configuration dump interval successfully set");
                request.getRequestDispatcher("/config.jsp").forward(request, response);
            }
            catch (Exception e) {
                request.setAttribute("errorMessage", e.getMessage());
                request.getRequestDispatcher("/config.jsp").forward(request, response);
            }
        }
    }
}