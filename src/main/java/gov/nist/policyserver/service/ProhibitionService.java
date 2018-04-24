package gov.nist.policyserver.service;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionRes;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static gov.nist.policyserver.dao.DAO.getDao;

public class ProhibitionService extends Service{
    public ProhibitionService() throws ConfigurationException {
        super();
    }

    public Prohibition createProhibition(String name, String[] operations, boolean intersection,
                                         ProhibitionRes[] resources, ProhibitionSubject denySubject)
            throws ProhibitionNameExistsException, DatabaseException, ConfigurationException {
        //check the prohibitions doesn't already exist
        Prohibition prohibition = access.getProhibition(name);
        if(prohibition != null){
            throw new ProhibitionNameExistsException(name);
        }

        //create prohibitions in database
        HashSet<String> hsOps = new HashSet<>(Arrays.asList(operations));
        getDao().createProhibition(name, hsOps, intersection, resources, denySubject);
        List<ProhibitionRes> rsrcs = Arrays.asList(resources);

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

    private Prohibition deleteProhibitionResource(String prohibitionName, long resourceId)
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

    private Prohibition setProhibitionSubject(String prohibitionName, long subjectId, String subjectType)
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

    public Prohibition updateProhibition(String name, String[] operations, ProhibitionRes[] resources, ProhibitionSubject subject) throws ProhibitionDoesNotExistException, ConfigurationException, DatabaseException, InvalidProhibitionSubjectTypeException, ProhibitionResourceDoesNotExistException, NodeNotFoundException, ProhibitionResourceExistsException {
        //check the prohibition exists
        Prohibition prohibition = getProhibition(name);

        //set operations
        getDao().setProhibitionOperations(prohibition.getName(), new HashSet<>(Arrays.asList(operations)));

        //set resources
        //remove existing resources
        List<ProhibitionRes> oldResources = prohibition.getResources();
        for(ProhibitionRes prohibitionRes : oldResources) {
            deleteProhibitionResource(prohibition.getName(), prohibitionRes.getResourceId());
        }

        //add new resources
        for(ProhibitionRes prohibitionRes : resources) {
            addResourceToProhibition(prohibition.getName(), prohibitionRes.getResourceId(), prohibitionRes.isCompliment());
        }

        //set subject
        setProhibitionSubject(prohibition.getName(), subject.getSubjectId(), subject.getSubjectType().toString());

        return getProhibition(name);
    }

    public List<Prohibition> getProhibitions(long subjectId, long resourceId) {
        List<Prohibition> prohibitions = access.getProhibitions();
        if(subjectId != 0) {
            prohibitions.removeIf(prohibition -> prohibition.getSubject().getSubjectId() != subjectId);
        }

        if(resourceId != 0) {
            prohibitions.removeIf(prohibition -> {
                List<ProhibitionRes> resources = prohibition.getResources();
                for(ProhibitionRes resource : resources) {
                    if(resource.getResourceId() == resourceId) {
                        return true;
                    }
                }
                return false;
            });
        }

        return prohibitions;
    }
}
