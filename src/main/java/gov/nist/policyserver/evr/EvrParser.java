package gov.nist.policyserver.evr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import gov.nist.policyserver.evr.model.exceptions.InvalidEvrException;
import gov.nist.policyserver.evr.model.script.Script;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class EvrParser {


    private final Element root;

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
        Script script = new Script(name);

        //get rules

        //for each rule
        //  get event
        //     get user-spec
        //     get pc-spec
        //     get op-spec
        //     get obj-spec
        //  get response
    }

    private String getName() throws InvalidEvrException {
        NodeList nodeList = root.getElementsByTagName("name");
        if(nodeList.getLength() == 0) {
            throw new InvalidEvrException("name tag required under root tag");
        }

        return nodeList.item(0).getTextContent();
    }

    public static void main(String[] args) {
        try {
            File inputFile = new File("scripts/evr2.evr");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("rule");
            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element: " + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    NodeList eventList = eElement.getElementsByTagName("event");
                    for(int j = 0; j < eventList.getLength(); j++) {
                        Element event = (Element) eventList.item(j);

                        NodeList spec = event.getElementsByTagName("user-spec");
                        System.out.println(spec.item(0).getNodeValue());

                        spec = event.getElementsByTagName("pc-spec");
                        System.out.println(spec.item(0));

                        Element opSpec = (Element) event.getElementsByTagName("op-spec").item(0);
                        NodeList ops = opSpec.getElementsByTagName("op");
                        for(int k = 0; k < ops.getLength(); k++) {
                            System.out.println(">"+ops.item(k).getTextContent());
                        }

                        spec = event.getElementsByTagName("obj-spec");
                        System.out.println(spec.item(0));

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
