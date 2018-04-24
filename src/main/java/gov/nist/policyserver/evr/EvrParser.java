package gov.nist.policyserver.evr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import gov.nist.policyserver.evr.model.*;
import gov.nist.policyserver.evr.model.script.EvrScript;
import gov.nist.policyserver.evr.model.script.rule.event.*;
import gov.nist.policyserver.evr.model.script.rule.event.time.EvrEvent;
import gov.nist.policyserver.evr.model.script.rule.event.time.EvrTime;
import gov.nist.policyserver.evr.model.script.rule.event.time.EvrTimeElement;
import gov.nist.policyserver.evr.model.script.rule.response.*;
import gov.nist.policyserver.exceptions.ConfigurationException;
import gov.nist.policyserver.exceptions.InvalidPropertyException;
import gov.nist.policyserver.model.graph.nodes.Property;
import gov.nist.policyserver.service.NodeService;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static gov.nist.policyserver.evr.EvrKeywords.*;

public class EvrParser {

    private static final  String IGNORE_CHAR     = "#";

    private Element root;
    private NodeService nodeService;

    public EvrParser() throws ConfigurationException {
        nodeService = new NodeService();
    }

    //init
    public EvrParser(File file) throws ParserConfigurationException, IOException, ConfigurationException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();

        nodeService = new NodeService();
    }

    //init
    public EvrParser(String xml) throws ParserConfigurationException, IOException, ConfigurationException, SAXException {
        loadScript(xml);
        nodeService = new NodeService();
    }

    public void loadScript(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }

    public EvrScript parseScript() throws InvalidEvrException, InvalidPropertyException {
        System.out.println("parsing script");

        //get label
        String name = getLabel(root);
        System.out.println("label: " + name);

        //initialize script object
        EvrScript script = new EvrScript(name);

        //parse rules and add
        List<EvrRule> evrRules = parseRules();
        script.setRules(evrRules);

        return script;
    }

    private String getLabel(Node node) {
        String name = null;

        List<Node> childNodes = getChildNodes(node);
        for(Node childNode : childNodes) {
            if(childNode.getNodeName().equals(LABEL_TAG)) {
                name = childNode.getTextContent();
            }
        }

        if(name == null) {
            name = UUID.randomUUID().toString().toUpperCase();
        }

        return name;
    }

    private List<EvrRule> parseRules() throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing rules...");
        List<EvrRule> rules = new ArrayList<>();

        Node rulesNode = null;
        List<Node> childNodes = getChildNodes(root);
        for(Node childNode : childNodes) {
            if(childNode.getNodeName().equals(RULES_TAG)) {
                rulesNode = childNode;
            }
        }

        if(rulesNode == null) {
            return rules;
        }

        childNodes = getChildNodes(rulesNode);
        for(Node childNode : childNodes) {
            if(childNode.getNodeName().equals(RULE_TAG)) {
                EvrRule evrRule = parseRule(childNode);
                rules.add(evrRule);
            }
        }

        return rules;
    }

    private EvrRule parseRule(Node ruleNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing rule...");
        EvrRule rule = new EvrRule();

        String label = getLabel(ruleNode);
        System.out.println("label: " + label);
        rule.setLabel(label);

        List<Node> childNodes = getChildNodes(ruleNode);
        for(Node childNode : childNodes) {
            if(childNode.getNodeName().equals(EVENT_TAG)) {
                EvrEvent event = parseEvent(childNode);
                rule.setEvent(event);
            }else if(childNode.getNodeName().equals(RESPONSE_TAG)) {
                EvrResponse response = parseResponse(childNode);
                rule.setResponse(response);
            }
        }

        return rule;
    }

    private EvrEvent parseEvent(Node eventNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing event...");
        EvrEvent event = new EvrEvent();

        List<Node> childNodes = getChildNodes(eventNode);
        for(Node childNode : childNodes) {
            if(childNode.getNodeName().equals(SUBJECT_TAG)) {
                EvrSubject subject = parseSubject(childNode);
                event.setSubject(subject);
            }else if(childNode.getNodeName().equals(OP_SPEC_TAG)) {
                EvrOpSpec opSpec = parseOpSpec(childNode);
                event.setOpSpec(opSpec);
            }else if(childNode.getNodeName().equals(PC_SPEC_TAG)) {
                EvrPcSpec pcSpec = parsePcSpec(childNode);
                event.setPcSpec(pcSpec);
            }else if(childNode.getNodeName().equals(TARGET_TAG)) {
                EvrTarget targetSpec = parseTarget(childNode);
                event.setTarget(targetSpec);
            } else if(childNode.getNodeName().equals(TIME_TAG)) {
                EvrTime evrTime = parseTime(childNode);
                event.setTime(evrTime);
            }
        }

        return event;
    }

    private EvrTime parseTime(Node timeNode) {
        System.out.println("parsing time event...");

        EvrTime evrTime = new EvrTime();

        List<Node> childNodes = getChildNodes(timeNode);
        for(Node node : childNodes) {
            EvrTimeElement element = parseTimeElement(node);
            switch (node.getNodeName()) {
                case DOW_TAG:
                    evrTime.setDow(element);
                    break;
                case DAY_TAG:
                    evrTime.setDay(element);
                    break;
                case MONTH_TAG:
                    evrTime.setMonth(element);
                    break;
                case YEAR_TAG:
                    evrTime.setYear(element);
                    break;
                case HOUR_TAG:
                    evrTime.setHour(element);
                    break;
            }
        }

        return evrTime;
    }

    private EvrTimeElement parseTimeElement(Node node) {
        System.out.println("parsing time element...");

        EvrTimeElement element = new EvrTimeElement();

        List<Node> childNodes = getChildNodes(node);
        if(childNodes.isEmpty()) {
            if(node.getChildNodes().getLength() > 0) {
                String[] split = node.getTextContent().split(",");
                List<Integer> values = new ArrayList<>();
                for(String s : split) {
                    values.add(new Integer(s));
                }
                element.setValues(values);
            }
        }else {
            //range
            int start = 0;
            int end = 0;
            for(Node child : childNodes) {
                if(child.getNodeName().equals(START_TAG)) {
                    start = Integer.valueOf(child.getTextContent());
                } else if (child.getNodeName().equals(END_TAG)) {
                    end = Integer.valueOf(child.getTextContent());
                }
            }
            element.setRange(start, end);
        }

        return element;
    }

    private EvrTarget parseTarget(Node targetNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing target spec...");
        EvrTarget targetSpec = new EvrTarget();

        //check if intersection
        NamedNodeMap attributes = targetNode.getAttributes();
        Node interNode = attributes.getNamedItem(INTERSECTION_ATTRIBUTE);
        boolean intersection = false;
        if(interNode != null) {
            intersection = Boolean.valueOf(interNode.getNodeValue());
        }
        targetSpec.setIntersection(intersection);

        List<Node> childNodes = getChildNodes(targetNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(ENTITY_TAG)) {
                System.out.println("target spec entity...");
                EvrEntity evrEntity = parseEntity(node);
                targetSpec.setEntity(evrEntity);
            } else if(node.getNodeName().equals(CONTAINER_TAG)) {
                List<Node> contChildNodes = getChildNodes(node);
                for(Node contChildNode : contChildNodes) {
                    if(contChildNode.getNodeName().equals(ENTITY_TAG)) {
                        System.out.println("target spec container...");
                        EvrEntity evrEntity = parseEntity(contChildNode);
                        targetSpec.addContainer(evrEntity);
                    }
                }
            }
        }
        return targetSpec;
    }

    private EvrPcSpec parsePcSpec(Node pcSpecNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing pc spec...");

        EvrPcSpec pcSpec = new EvrPcSpec();

        //child tags are either OR or AND.  If there are no child nodes
        //the default is OR
        List<Node> childNodes = getChildNodes(pcSpecNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(AND_TAG)) {
                pcSpec.setOr(false);
                pcSpecNode = node;
            } else if(node.getNodeName().equals(OR_TAG)) {
                pcSpecNode = node;
            }
        }

        //get actual entity nodes
        childNodes = getChildNodes(pcSpecNode);
        for(Node child : childNodes) {
            if(child.getNodeName().equals(ENTITY_TAG)) {
                EvrEntity evrEntity = parseEntity(child);
                if(!evrEntity.isAny()) {
                    pcSpec.addEntity(evrEntity);
                }
            }
        }

        return pcSpec;
    }

    private EvrOpSpec parseOpSpec(Node opSpecNode) {
        System.out.println("parsing op spec...");

        List<String> ops = new ArrayList<>();

        List<Node> childNodes = getChildNodes(opSpecNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(OP_TAG)) {
                if(node.getTextContent().length() > 0) {
                    ops.add(node.getTextContent());
                }
            }
        }

        System.out.println("\tops: " + ops);

        return new EvrOpSpec(ops);
    }

    private EvrSubject parseSubject(Node subjectNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing subject...");

        EvrSubject subject = new EvrSubject();

        List<Node> childNodes = getChildNodes(subjectNode);
        //if childnodes is not empty then add to the list, else do nothing. An empty list means any user
        if(!childNodes.isEmpty()) {
            for(Node node : childNodes) {
                if(node.getNodeName().equals(ENTITY_TAG)) {
                    EvrEntity evrEntity = parseEntity(node);
                    //if the entity is any entity dont add to subject
                    if(!evrEntity.isAny()) {
                        subject.addEntity(evrEntity);
                    }
                } else if(node.getNodeName().equals(FUNCTION_TAG)) {
                    EvrFunction evrFunction = parseFunction(node);
                    subject.addEntity(new EvrEntity(evrFunction));
                } else if(node.getNodeName().equals(PROCESS_TAG)) {
                    EvrProcess evrProcess = parseProcess(node);
                    subject.addEntity(new EvrEntity(evrProcess));
                }
            }
        }

        return subject;
    }

    private EvrProcess parseProcess(Node processNode) throws InvalidEvrException, InvalidPropertyException {
        System.out.println("parsing process...");

        EvrProcess evrProcess = new EvrProcess();

        List<Node> childNodes = getChildNodes(processNode);
        if(childNodes.isEmpty()) {
            evrProcess = new EvrProcess(processNode.getTextContent());
        }else {
            //arg is a function
            for(Node argChild : childNodes) {
                if(argChild.getNodeName().equals(FUNCTION_TAG)) {
                    EvrFunction evrFunction = parseFunction(argChild);
                    evrProcess = new EvrProcess(evrFunction);
                }
            }
        }

        if(!evrProcess.isFunction()) {
            System.out.println("\tprocessId: " + evrProcess.getProcessId());
        }

        return evrProcess;
    }

    private EvrFunction parseFunction(Node functionNode) throws InvalidEvrException, InvalidPropertyException {
        NamedNodeMap attributes = functionNode.getAttributes();
        Node name = attributes.getNamedItem(NAME_ATTRIBUTE);

        EvrFunction evrFunction = new EvrFunction(name.getNodeValue());

        List<Node> childNodes = getChildNodes(functionNode);
        //loop through arguments
        for(Node node : childNodes) {
            List<Node> argChildren = getChildNodes(node);
            if(argChildren.isEmpty()) {
                //arg is a value
                evrFunction.addArg(new EvrArg(node.getTextContent()));
            } else {
                //arg is a function
                for(Node argChild : argChildren) {
                    if(argChild.getNodeName().equals(FUNCTION_TAG)) {
                        EvrFunction evrFunctionArg = parseFunction(argChild);
                        evrFunction.addArg(new EvrArg(evrFunctionArg));
                    } else if(argChild.getNodeName().equals(ENTITY_TAG)) {
                        EvrEntity entity = parseEntity(argChild);
                        evrFunction.addArg(new EvrArg(entity));
                    }
                }
            }
        }

        System.out.println("parsing function...");
        System.out.println("\tfunction name: " + evrFunction.getFunctionName());
        for(EvrArg arg : evrFunction.getArgs()) {
            if(arg.isFunction()) {
                System.out.println("\targ: " + arg.getFunction().getFunctionName() + "()");
            } else if(arg.isEntity()) {
                System.out.println("\targ: " + arg.getEntity().getName());
            } else {
                System.out.println("\targ: " + arg.getValue());
            }
        }

        return evrFunction;
    }

    private EvrEntity parseEntity(Node entityNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing entity...");

        NamedNodeMap attributes = entityNode.getAttributes();
        Node name = attributes.getNamedItem(NAME_ATTRIBUTE);
        Node type = attributes.getNamedItem(TYPE_ATTRIBUTE);
        Node properties = attributes.getNamedItem(PROPERTIES_ATTRIBUTE);
        Node compliment = attributes.getNamedItem(COMP_ATTRIBUTE);

        EvrEntity evrEntity = null;

        List<Node> childNodes = getChildNodes(entityNode);
        if(childNodes.isEmpty()) {
            //node
            List<Property> propList = new ArrayList<>();
            if(properties != null) {
                String[] propArr = properties.getNodeValue().split(",");
                for(String prop : propArr) {
                    String[] pieces = prop.split("=");
                    propList.add(new Property(pieces[0], pieces[1]));
                }
            }

            String entityName = null;
            if(name != null) {
                entityName = name.getNodeValue();
            }

            String entityType = null;
            if(type != null) {
                entityType = type.getNodeValue();
            }

            boolean bComp = false;
            if(compliment != null) {
                bComp = Boolean.valueOf(compliment.getNodeValue());
            }

            System.out.println("\tname=" + entityName);
            System.out.println("\ttype=" + entityType);
            System.out.println("\tproperties=" + propList);

            evrEntity = new EvrEntity(entityName, entityType, propList, bComp);
        } else {
            //function
            for(Node node : childNodes) {
                if(node.getNodeName().equals(FUNCTION_TAG)) {
                    evrEntity = new EvrEntity(parseFunction(node));
                }
            }
        }


        return evrEntity;
    }

    private EvrResponse parseResponse(Node responseNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing response...");

        EvrResponse response = new EvrResponse();

        List<Node> childNodes = getChildNodes(responseNode);
        for(Node node : childNodes) {
            switch(node.getNodeName()) {
                case CONDITION_TAG:
                    EvrCondition condition = parseCondition(node);
                    response.setCondition(condition);
                    break;
                case ASSIGN_TAG:
                    EvrAssignAction evrAssignAction = parseAssign(node);
                    response.addAction(evrAssignAction);
                    break;
                case GRANT_TAG:
                    EvrGrantAction evrGrantAction = parseGrant(node);
                    response.addAction(evrGrantAction);
                    break;
                case CREATE_TAG:
                    EvrCreateAction evrCreateAction = parseCreate(node);
                    response.addAction(evrCreateAction);
                    break;
                case DENY_TAG:
                    EvrDenyAction evrDenyAction = parseDeny(node);
                    response.addAction(evrDenyAction);
                    break;
                case DELETE_TAG:
                    EvrDeleteAction evrDeleteAction = parseDelete(node);
                    response.addAction(evrDeleteAction);
                    break;
            }
        }
        return response;
    }

    private EvrDeleteAction parseDelete(Node deleteNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing deny action...");

        EvrDeleteAction action = new EvrDeleteAction();

        List<Node> childNodes = getChildNodes(deleteNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(ASSIGN_TAG)) {
                EvrAssignAction evrAssignAction = parseAssign(node);
                action.setEvrAction(evrAssignAction);
            } else if(node.getNodeName().equals(DENY_TAG)) {
                EvrDenyAction evrDenyAction = parseDeny(node);
                action.setEvrAction(evrDenyAction);
            } else if(node.getNodeName().equals(RULE_TAG)) {
                EvrRule evrRule = parseRule(node);
                action.setEvrRule(evrRule);
            }
        }

        return action;
    }

    private EvrDenyAction parseDeny(Node denyNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing deny action...");

        EvrDenyAction action = new EvrDenyAction();

        List<Node> childNodes = getChildNodes(denyNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(SUBJECT_TAG)) {
                EvrSubject evrSubject = parseSubject(node);
                action.setSubject(evrSubject);
            } else if(node.getNodeName().equals(OP_SPEC_TAG)) {
                EvrOpSpec evrOpSpec = parseOpSpec(node);
                action.setOpSpec(evrOpSpec);
            } else if(node.getNodeName().equals(TARGET_TAG)) {
                EvrTarget evrTarget = parseTarget(node);
                action.setTarget(evrTarget);
            }
        }

        return action;
    }

    private EvrCreateAction parseCreate(Node createNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing create action...");

        EvrCreateAction action = new EvrCreateAction();

        List<Node> childNodes = getChildNodes(createNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(ENTITY_TAG)) {
                EvrEntity entity = parseEntity(node);
                action.setEntity(entity);
            } else if(node.getNodeName().equals(TARGET_TAG)) {
                EvrTarget evrTarget = parseTarget(node);
                action.setTarget(evrTarget);
            } else if(node.getNodeName().equals(RULE_TAG)) {
                EvrRule evrRule = parseRule(node);
                action.setRule(evrRule);
            }
        }

        return action;
    }

    private EvrGrantAction parseGrant(Node grantNode) throws InvalidEvrException, InvalidPropertyException {
        System.out.println("parsing grant action...");

        EvrGrantAction action = new EvrGrantAction();

        List<Node> childNodes = getChildNodes(grantNode);
        for(Node node : childNodes) {
            if(node.getNodeName().equals(SUBJECT_TAG)) {
                EvrSubject evrSubject = parseSubject(node);
                action.setSubject(evrSubject);
            } else if(node.getNodeName().equals(OP_SPEC_TAG)) {
                EvrOpSpec evrOpSpec = parseOpSpec(node);
                action.setOpSpec(evrOpSpec);
            } else if(node.getNodeName().equals(TARGET_TAG)) {
                EvrTarget evrTarget = parseTarget(node);
                action.setTarget(evrTarget);
            }
        }

        return action;
    }

    private EvrAssignAction parseAssign(Node assignNode) throws InvalidPropertyException, InvalidEvrException {
        System.out.println("parsing assign action...");

        EvrAssignAction action = new EvrAssignAction();

        List<Node> childNodes = getChildNodes(assignNode);

        for(Node node : childNodes) {
            List<Node> childChildNodes = getChildNodes(node);

            Node childNode = childChildNodes.get(0);
            EvrEntity evrEntity = new EvrEntity();

            if(childNode.getNodeName().equals(ENTITY_TAG)) {
                evrEntity = parseEntity(childNode);
            } else if(childNode.getNodeName().equals(FUNCTION_TAG)) {
                evrEntity = new EvrEntity(parseFunction(childNode));
            }

            if(node.getNodeName().equals(CHILD_TAG)) {
                action.setChild(evrEntity);
            } else if(node.getNodeName().equals(PARENT_TAG)) {
                action.setParent(evrEntity);
            }
        }

        return action;
    }

    private EvrCondition parseCondition(Node conditionNode) throws InvalidPropertyException, InvalidEvrException {
        NamedNodeMap attributes = conditionNode.getAttributes();
        Node existsNode = attributes.getNamedItem(EXISTS_ATTRIBUTE);
        boolean exists = (existsNode == null) ? true : Boolean.valueOf(existsNode.getNodeValue());

        EvrCondition condition = new EvrCondition();
        condition.setExists(exists);

        List<Node> childNodes = getChildNodes(conditionNode);

        for(Node node : childNodes) {
            if(node.getNodeName().equals(ENTITY_TAG)) {
                EvrEntity entity = parseEntity(node);
                condition.setEntity(entity);
            } else if(node.getNodeName().equals(FUNCTION_TAG)) {
                EvrFunction evrFunction = parseFunction(node);
                condition.setEntity(new EvrEntity(evrFunction));
            }
        }

        return condition;
    }

    private List<Node> getChildNodes(Node node) {
        List<Node> nodes = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            if(!childNodes.item(i).getNodeName().startsWith(IGNORE_CHAR)) {
                nodes.add(childNodes.item(i));
            }
        }

        return nodes;
    }



    public static void main(String[] args) {
        try {
            File inputFile = new File("scripts/test.evr");

            EvrParser parser = new EvrParser(inputFile);
            parser.parseScript();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}