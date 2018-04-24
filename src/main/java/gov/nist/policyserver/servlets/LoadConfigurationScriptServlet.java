package gov.nist.policyserver.servlets;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.service.ConfigurationService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class LoadConfigurationScriptServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        DiskFileItemFactory factory = new DiskFileItemFactory();

        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);

        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(request);
            System.out.println(items);
            for(FileItem item : items) {
                if (!item.isFormField()) {
                    InputStream uploadedStream = item.getInputStream();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(uploadedStream, writer, "UTF-8");
                    String config = writer.toString();

                    ConfigurationService service = new ConfigurationService();
                    service.load(config);
                }
            }

            DAO.getDao().buildGraph();

            request.getRequestDispatcher("/config.jsp?display=block&result=success&message=Configuration+loaded+successfully").forward(request, response);

        }
        catch (NodeIdExistsException | FileUploadException | ConfigurationException |
                InvalidPropertyException | AssignmentExistsException | DatabaseException |
                NodeNameExistsException | NodeNotFoundException | NodeNameExistsInNamespaceException |
                NullNameException | NullTypeException | InvalidNodeTypeException | AssociationExistsException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            request.getRequestDispatcher("/config.jsp?display=block&result=danger&message=" + e.getMessage().replaceAll(" ", "+")).forward(request, response);
        }
    }
}