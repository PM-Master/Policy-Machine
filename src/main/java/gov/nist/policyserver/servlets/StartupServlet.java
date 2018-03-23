package gov.nist.policyserver.servlets;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.DatabaseException;

import javax.servlet.http.HttpServlet;

public class StartupServlet extends HttpServlet {

    @Override
    public void init() {
        try {
            DAO.init();
        }
        catch (DatabaseException | ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
