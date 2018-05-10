package gov.nist.policyserver.service;

import gov.nist.policyserver.dao.DAO;
import gov.nist.policyserver.exceptions.*;
import gov.nist.policyserver.model.graph.nodes.Node;
import gov.nist.policyserver.model.prohibitions.Prohibition;
import gov.nist.policyserver.model.prohibitions.ProhibitionResource;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubject;
import gov.nist.policyserver.model.prohibitions.ProhibitionSubjectType;

import java.util.*;

import static gov.nist.policyserver.dao.DAO.getDao;

public class ProhibitionsService extends Service{
    public ProhibitionsService() throws ConfigurationException {
        super();
    }

    public Prohibition createProhibition(String name, String[] operations, boolean intersection,
                                         ProhibitionResource[] resources, ProhibitionSubject denySubject)
            throws ProhibitionNameExistsException, DatabaseException, ConfigurationException, NullNameException {
        if(name == null || name.isEmpty()) {
            throw new NullNameException();
        }

        //check the prohibitions doesn't already exist
        Prohibition prohibition = access.getProhibition(name);
        if(prohibition != null){
            throw new ProhibitionNameExistsException(name);
        }

        HashSet<String> hsOps = new HashSet<>(Arrays.asList(operations));

        //create prohibition in database
        getDao().createProhibition(name, hsOps, intersection, resources, denySubject);

        List<ProhibitionResource> resourcesList = new ArrayList<>();
        Collections.addAll(resourcesList, resources);

        //add prohibition to nodes
        prohibition = new Prohibition(denySubject, resourcesList, name, hsOps, intersection);
        access.addProhibition(prohibition);

        return prohibition;
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
        //check that the prohibition exists
        getProhibition(prohibitionName);

        //delete prohibition in database
        DAO.getDao().deleteProhibition(prohibitionName);

        //delete prohibition in memory
        access.removeProhibition(prohibitionName);
    }

    private void addResourceToProhibition(String prohibitionName, long resourceId, boolean complement)
            throws DatabaseException, ProhibitionDoesNotExistException, NodeNotFoundException, ProhibitionResourceExistsException, ConfigurationException {
        //check if the prohibition exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //check if the resource exists
        Node node = graph.getNode(resourceId);
        if(node == null){
            throw new NodeNotFoundException(resourceId);
        }

        //check if resource exists in prohibition
        ProhibitionResource pr = new ProhibitionResource(resourceId, complement);
        if(prohibition.getResources().contains(pr)){
            throw new ProhibitionResourceExistsException(prohibitionName, resourceId);
        }
        //add resource to prohibition in database
        getDao().addResourceToProhibition(prohibitionName, resourceId, complement);

        //add resource to prohibition in memory
        prohibition.addResource(pr);

    }

    private void setProhibitionSubject(String prohibitionName, long subjectId, String subjectType)
            throws InvalidProhibitionSubjectTypeException, DatabaseException, ProhibitionDoesNotExistException, ConfigurationException {
        ProhibitionSubjectType subType = ProhibitionSubjectType.toProhibitionSubjectType(subjectType);

        //check if prohibition exists
        Prohibition prohibition = getProhibition(prohibitionName);

        //set the prohibition subject in the database
        getDao().setProhibitionSubject(prohibitionName, subjectId, subType);

        //set the prohibition subject in memory
        prohibition.setSubject(new ProhibitionSubject(subjectId, subType));
    }

    public Prohibition updateProhibition(String name, boolean intersection, String[] operations, ProhibitionResource[] resources, ProhibitionSubject subject) throws ProhibitionDoesNotExistException, ConfigurationException, DatabaseException, InvalidProhibitionSubjectTypeException, NodeNotFoundException, ProhibitionResourceExistsException {
        //check the prohibition exists
        Prohibition prohibition = getProhibition(name);

        //set intersection
        getDao().setProhibitionIntersection(name, intersection);
        prohibition.setIntersection(intersection);

        //set operations
        HashSet<String> opSet = new HashSet<>(Arrays.asList(operations));
        getDao().setProhibitionOperations(prohibition.getName(), opSet);
        prohibition.setOperations(opSet);

        //set resources
        List<ProhibitionResource> oldResources = prohibition.getResources();

        //delete from database
        for(ProhibitionResource prohibitionResource : oldResources) {
            getDao().deleteProhibitionResource(name, prohibitionResource.getResourceId());
        }
        //remove from memory
        prohibition.clearResources();


        //add new resources
        for(ProhibitionResource prohibitionResource : resources) {
            addResourceToProhibition(prohibition.getName(), prohibitionResource.getResourceId(), prohibitionResource.isComplement());
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
                List<ProhibitionResource> resources = prohibition.getResources();
                for(ProhibitionResource resource : resources) {
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
