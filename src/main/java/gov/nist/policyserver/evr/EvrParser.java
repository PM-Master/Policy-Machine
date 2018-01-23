package gov.nist.policyserver.evr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import gov.nist.policyserver.evr.model.script.Script;
import gov.nist.policyserver.evr.model.script.rule.Rule;
import gov.nist.policyserver.evr.model.script.rule.event.Event;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EvrParser {
    private final String SCRIPT_TAG    = "script";
    private final String RULES_TAG     = "rules";
    private final String RULE_TAG      = "rule";
    private final String EVENT_TAG     = "event";
    private final String RESPONSE_TAG  = "response";
    private final String SUBJECT_TAG   = "subject";
    private final String PC_SPEC_TAG   = "pc-spec";
    private final String OP_SPEC_TAG   = "op-spec";
    private final String TARGET_TAG    = "target";



    private final Element root;
    private Script script;

    //init
    public EvrParser(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }

    //init
    public EvrParser(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));

        Document doc = db.parse(is);
        doc.getDocumentElement().normalize();
        root = doc.getDocumentElement();
    }

    public void parse() throws InvalidEvrException {
        //get name
        String name = getName();
        System.out.println("script: " + name);

        script = new Script(name);

        NodeList ruleElements = root.getElementsByTagName(RULES_TAG);
        if(ruleElements.getLength() > 0) {
            System.out.println("\trules:");

            Node rulesNode = ruleElements.item(0);
            NodeList ruleNodes = rulesNode.getChildNodes();

            //loop through each rule
            for(int i = 0; i < ruleNodes.getLength(); i++) {
                Node ruleItem = ruleNodes.item(i);

                //parse rule
                if(ruleItem.getNodeName().equals(RULE_TAG)) {
                    System.out.println("\t\trule:");
                    Rule rule = new Rule();

                    //rule children are event and response
                    NodeList childNodes = ruleItem.getChildNodes();
                    for(int j = 0; j < childNodes.getLength(); j++) {
                        Node ruleChildItem = childNodes.item(j);

                        if(ruleChildItem.getNodeName().equals(EVENT_TAG)) {
                            System.out.println("\t\t\tevent:");
                            Event event = new Event();

                            //get user spec
                            NodeList eventChildNodes = ruleChildItem.getChildNodes();
                            for(int k = 0; k < eventChildNodes.getLength(); k++) {
                                Node eventChildItem = eventChildNodes.item(k);

                                //user, pc, op, obj specs
                                NodeList specNodes = eventChildItem.getChildNodes();
                                if(eventChildItem.getNodeName().equals(SUBJECT_TAG)) {
                                    System.out.println("\t\t\t\tsubject:");
                                    System.out.println("\t\t\t\t\t");
                                } else if(eventChildItem.getNodeName().equals(PC_SPEC_TAG)) {
                                    System.out.println("\t\t\t\tpc-spec:");
                                } else if(eventChildItem.getNodeName().equals(OP_SPEC_TAG)) {
                                    System.out.println("\t\t\t\top-spec:");
                                } else if(eventChildItem.getNodeName().equals(TARGET_TAG)) {
                                    System.out.println("\t\t\t\ttarget:");
                                }
                            }

                        } else if (ruleChildItem.getNodeName().equals("response")) {
                            System.out.println("\t\t\tresponse:");
                        }
                    }

                    script.addRule(new Rule());
                }
            }

        }

        //get rules
        //for each rule
        //  get event
        //     get user-spec
        //     get pc-spec
        //     get op-spec
        //     get obj-spec
        //  get response
    }

    /**
     * add rule objects to script object
     * return rule elements<<
     * @return
     */
    /*private List<Node> getRules() {
        List<Node> rules = new ArrayList<>();

        NodeList ruleElements = root.getElementsByTagName("rules");
        if(ruleElements.getLength() > 0) {
            Node rulesNode = ruleElements.item(0);
            NodeList ruleNodes = rulesNode.getChildNodes();
            for(int i = 0; i < ruleNodes.getLength(); i++) {
                Node item = ruleNodes.item(i);
                if(item.getNodeName().equals("rule")) {
                    rules.add(item);
                    script.addRule(new Rule());
                }
            }

        }
        return rules;
    }*/

    private String getName() throws InvalidEvrException {
        NodeList nodeList = root.getElementsByTagName("name");
        if(nodeList.getLength() == 0) {
            throw new InvalidEvrException("name tag required under root tag");
        }

        return nodeList.item(0).getTextContent();
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("scripts/evr3.evr");

            EvrParser parser = new EvrParser(inputFile);
            parser.parse();

            /*DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("rule");
            //System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element: " + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList eventList = eElement.getElementsByTagName("event");
                    for(int j = 0; j < eventList.getLength(); j++) {
                        Element event = (Element) eventList.item(j);

                        NodeList spec = event.getElementsByTagName("user-spec");
                        //System.out.println(spec.item(0).getNodeValue());

                        spec = event.getElementsByTagName("pc-spec");
                        //System.out.println(spec.item(0));

                        Element opSpec = (Element) event.getElementsByTagName("op-spec").item(0);
                        NodeList ops = opSpec.getElementsByTagName("op");
                        for(int k = 0; k < ops.getLength(); k++) {
                            //System.out.println(">"+ops.item(k).getTextContent());
                        }

                        spec = event.getElementsByTagName("obj-spec");
                        //System.out.println(spec.item(0));

                    }
                }
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
