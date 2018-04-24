package gov.nist.policyserver.servlets;

import gov.nist.policyserver.service.ConfigurationService;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SaveConfigurationScriptServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException
    {
        try {
            System.out.println(System.getProperty("user.dir"));
            ConfigurationService service = new ConfigurationService();
            String configuration = service.save();

            String configName = request.getParameter("configName");

            response.setHeader("Content-Disposition", "attachment; filename=\"" + configName +".pm\"");
            response.setContentType("application/octet-stream");

            StringBuffer sb = new StringBuffer(configuration);
            InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
            ServletOutputStream out = response.getOutputStream();

            out.write(sb.toString().getBytes(), 0, sb.toString().length());
            in.close();
            out.flush();
            out.close();

            request.getRequestDispatcher("/config.jsp?display=block&result=success&message=Configuration+saved").forward(request, response);
        }
        catch (Exception e) {
            request.getRequestDispatcher("/config.jsp?display=block&result=error&message=" + e.getMessage().replaceAll(" ", "+")).forward(request, response);
        }
    }
}
