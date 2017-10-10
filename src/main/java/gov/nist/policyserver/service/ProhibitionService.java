package gov.nist.policyserver.service;

import gov.nist.policyserver.access.PmAccess;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.graph.PmGraph;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.dao.DAO.getDao;

public class ProhibitionService {

    private PmGraph  graph;
    private PmAccess access;

    public ProhibitionService() throws ConfigurationException {
        graph = getDao().getGraph();
        access = getDao().getAccess();
    }

    public Prohibition createProhibition(String name, String[] operations, boolean intersection,
                                         ProhibitionRes[] resources, ProhibitionSubject denySubject)
            throws ProhibitionNameExistsException, DatabaseException, InvalidProhibitionSubjectTypeException, ProhibitionDoesNotExistException, NodeNotFoundException, ProhibitionResourceExistsException, ConfigurationException {
        //check the prohibitions doesn't already exist
        Prohibition prohibition = access.getProhibition(name);
        if(prohibition != null){
            throw new ProhibitionNameExistsException(name);
        }

        //create prohibitions in database
        HashSet<String> hsOps = new HashSet<>(Arrays.asList(operations));
        getDao().createProhibition(name, hsOps, intersection, resources, denySubject);
        List<ProhibitionRes> rsrcs = (List<ProhibitionRes>) Arrays.asList(resources);
        System.out.println(rsrcs.size());
        //add prohibitions to nodes
        prohibition = new Prohibition(denySubject, rsrcs, name, hsOps, intersection);
        access.addProhibition(prohibition);
        return access.getProhibition(name);
    }

    public Prohibition getProhibition(String prohibitionName)
            throws ProhibitionDoesNotExistException {
        Prohibition prohibition = access.getProhibition(prohibitionName);
        if(prohibition == null){
            throw new ProhibitionDoesNotExistException(prohibitionName);
        }

        return prohibition;
    }

    public void deleteProhibition(String prohibitionName)
            throws ProhibitionDoesNotExistException, DatabaseException, ConfigurationException {
        //check that the prohibitions exists
        getProhibition(prohibitionName);

        //delete prohibitions in database
        DAO.getDao().deleteProhibition(prohibitionName);

        //delete prohibitions in memory
        access.removeProhibition(prohibitionName);
    }

    public Prohibition addResourceToProhibition(String prohibitionName, long resourceId, boolean compliment)
            throws DatabaseException, ProhibitionDoesNotExistException, NodeNotFoundException, ProhibitionResourceExistsException, ConfigurationException {
        //check if the prohibitions exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //check if the resource exists
        Node node = graph.getNode(resourceId);
        if(node == null){
            throw new NodeNotFoundException(resourceId);
        }

        //check if resource exists in prohibitions
        ProhibitionRes pr = new ProhibitionRes(resourceId, compliment);
        if(prohibition.getResources().contains(pr)){
            throw new ProhibitionResourceExistsException(prohibitionName, resourceId);
        }
        System.out.println(pr.getResourceId());
        //add resource to prohibitions in database
        getDao().addResourceToProhibition(prohibitionName, resourceId, compliment);

        //add resource to prohibitions in memory
        prohibition.addResource(pr);

        return prohibition;
    }

    public List<ProhibitionRes> getProhibitionResources(String prohibitionName)
            throws ProhibitionDoesNotExistException {
        Prohibition prohibition = getProhibition(prohibitionName);

        return prohibition.getResources();
    }

    public ProhibitionRes getProhibitionResource(String prohibitionName, long resourceId)
            throws ProhibitionDoesNotExistException, ProhibitionResourceDoesNotExistException {
        //get the prohibitions
        Prohibition prohibition = getProhibition(prohibitionName);

        //find the resource given
        for(ProhibitionRes pr : prohibition.getResources()){
            if(pr.getResourceId()==resourceId){
                return pr;
            }
        }

        //if no resource is found throw exception
        throw new ProhibitionResourceDoesNotExistException(prohibitionName, resourceId);
    }

    public Prohibition deleteProhibitionResource(String prohibitionName, long resourceId)
            throws ProhibitionDoesNotExistException, DatabaseException, ProhibitionResourceDoesNotExistException, ConfigurationException {
        //check if prohibitions exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //check if resource exists in prohibitions
        boolean found = false;
        List<ProhibitionRes> resources = prohibition.getResources();
        for (ProhibitionRes resource : resources) {
            if (!found && resource.getResourceId() == resourceId) {
                prohibition.removeResource(resourceId);
                found = true;
            }
        }
        if (!found) {
            throw new ProhibitionResourceDoesNotExistException(prohibitionName, resourceId);
        }

        //delete resource form prohibitions in database
        getDao().deleteProhibitionResource(prohibitionName, resourceId);

        //delete resource from prohibitions in memory
        prohibition.removeResource(resourceId);

        return prohibition;
    }

    public Prohibition setProhibitionSubject(String prohibitionName, long subjectId, String subjectType)
            throws InvalidProhibitionSubjectTypeException, DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        ProhibitionSubjectType subType = ProhibitionSubjectType.toProhibitionSubjectType(subjectType);

        //check if prohibitions exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //set the prohibitions subject in the database
        getDao().setProhibitionSubject(prohibitionName, subjectId, subType);

        //set the prohibitions subject in memory
        prohibition.setSubject(new ProhibitionSubject(subjectId, subType));

        return prohibition;
    }

    public ProhibitionSubject getProhibitionSubject(String prohibitionName)
            throws ProhibitionDoesNotExistException, ProhibitionSubjectDoesNotExistException {
        Prohibition prohibition = getProhibition(prohibitionName);

        ProhibitionSubject ps = prohibition.getSubject();
        if(ps == null){
            throw new ProhibitionSubjectDoesNotExistException(prohibitionName);
        }

        return prohibition.getSubject();
    }

    public Prohibition addOperationsToProhibition(String prohibitionName, String[] operations)
            throws ProhibitionDoesNotExistException, DatabaseException, ConfigurationException {
        HashSet<String> opSet = new HashSet<>(Arrays.asList(operations));

        //check if prohibitions exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //add existing ops to new ones
        opSet.addAll(prohibition.getOperations());

        //add the operations in the database
        getDao().setProhibitionOperations(prohibitionName, opSet);

        //set the prohibitions subject in memory
        prohibition.setOperations(opSet);

        return prohibition;
    }

    public HashSet<String> getProhibitionOperations(String prohibitionName)
            throws ProhibitionDoesNotExistException {
        Prohibition prohibition = getProhibition(prohibitionName);

        return prohibition.getOperations();
    }

    public Prohibition removeOperationFromProhibition(String prohibitionName, String op)
            throws ProhibitionDoesNotExistException, DatabaseException, ConfigurationException {
        //check if prohibitions exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //remove op from prohibitions in memory
        prohibition.getOperations().remove(op);

        //remove the operation in the database
        getDao().setProhibitionOperations(prohibitionName, prohibition.getOperations());

        return prohibition;
    }
}
