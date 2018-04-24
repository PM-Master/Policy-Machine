package gov.nist.policyserver.service;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.evr.EvrManager;
import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.InvalidPropertyException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class EvrService {

    private EvrManager evrManager;

    public EvrService() throws ConfigurationException {
        super();

        evrManager = DAO.getDao().getEvrManager();
    }
    public void processSql(String sqlId) {
        System.out.println("sql with id = " + sqlId + " was successful, making changes in pm now...");
        evrManager.removeActiveSql(sqlId);
    }

    public void load(String script) throws IOException, InvalidEvrException, ParserConfigurationException, SAXException, InvalidPropertyException {
        evrManager.parse(script);
    }
}
