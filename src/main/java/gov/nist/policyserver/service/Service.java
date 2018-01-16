package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.graph.PmGraph;

import static gov.nist.policyserver.dao.DAO.getDao;

public class Service {
    public PmGraph graph;
    public PmAccess access;

    public Service() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();
    }
}
