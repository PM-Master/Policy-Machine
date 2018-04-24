package gov.nist.policyserver.evr;

import gov.nist.policyserver.evr.exceptions.InvalidEvrException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gov.nist.policyserver.evr.EvrKeywords.*;

public class EvrValidator {
    private static List<String> script = Arrays.asList("label", "rules");
    private static List<String> label = Arrays.asList();
    private static List<String> rules = Arrays.asList("rule");
    private static List<String> rule = Arrays.asList("event", "response");
    private static List<String> event = Arrays.asList("subject", "pc-spec", "op-spec", "target");
    private static List<String> subject = Arrays.asList("entity", "function", "process");
    private static List<String> entity = Arrays.asList("function");
    private static List<String> function = Arrays.asList("arg");
    private static List<String> arg = Arrays.asList("function");
    private static List<String> process = Arrays.asList("function");
    private static List<String> pcSpec = Arrays.asList("entity", "or", "and");
    private static List<String> or = Arrays.asList("entity");
    private static List<String> and = Arrays.asList("entity");
    private static List<String> opSpec = Arrays.asList("op");
    private static List<String> target = Arrays.asList("entity", "container");
    private static List<String> container = Arrays.asList("entity", "function");
    private static List<String> time = Arrays.asList("dow", "month", "year", "hour");
    private static List<String> dow = Arrays.asList("start", "end");
    private static List<String> month = Arrays.asList("start", "end");
    private static List<String> year = Arrays.asList("start", "end");
    private static List<String> hour = Arrays.asList("start", "end");
    private static List<String> condition = Arrays.asList("entity");
    private static List<String> assign = Arrays.asList("child", "parent");
    private static List<String> grant = Arrays.asList("subject", "op-spec", "target");
    private static List<String> create = Arrays.asList("subject", "target", "rule");
    private static List<String> deny = Arrays.asList("subject", "op-spec", "target");
    private static List<String> delete = Arrays.asList("assign", "deny", "rule");

    private static final String IGNORE_CHAR       = "#";
    private static final int NO_LIMIT = -1;
    private static final int LIMIT = 1;

    private static HashMap<String, List<String>> allowedTags = new HashMap();
    {
        allowedTags.put("script", Arrays.asList("label", "rules"));
        allowedTags.put("label", Arrays.asList());
        allowedTags.put("rules", Arrays.asList("rule"));
        allowedTags.put("rule", Arrays.asList("event", "response"));
        allowedTags.put("event", Arrays.asList("subject", "pc-spec", "op-spec", "target"));
        allowedTags.put("subject", Arrays.asList("entity", "function", "process"));
        allowedTags.put("entity", Arrays.asList("function"));
        allowedTags.put("function", Arrays.asList("arg"));
        allowedTags.put("arg", Arrays.asList("function"));
        allowedTags.put("process", Arrays.asList("function"));
        allowedTags.put("pc-spec", Arrays.asList("entity", "or", "and"));
        allowedTags.put("or", Arrays.asList("entity"));
        allowedTags.put("and", Arrays.asList("entity"));
        allowedTags.put("op-spec", Arrays.asList("op"));
        allowedTags.put("target", Arrays.asList("entity", "container"));
        allowedTags.put("container", Arrays.asList("entity", "function"));
        allowedTags.put("time", Arrays.asList("dow", "month", "year", "hour"));
        allowedTags.put("dow", Arrays.asList("start", "end"));
        allowedTags.put("month", Arrays.asList("start", "end"));
        allowedTags.put("year", Arrays.asList("start", "end"));
        allowedTags.put("hour", Arrays.asList("start", "end"));
        allowedTags.put("condition", Arrays.asList("entity"));
        allowedTags.put("assign", Arrays.asList("child", "parent"));
        allowedTags.put("grant", Arrays.asList("subject", "op-spec", "target"));
        allowedTags.put("create", Arrays.asList("subject", "target", "rule"));
        allowedTags.put("deny", Arrays.asList("subject", "op-spec", "target"));
        allowedTags.put("delete", Arrays.asList("assign", "deny", "rule"));
    }

    public static void validate(Node node) throws InvalidEvrException {
        switch (node.getNodeName()) {
            case "script":
                validateScript(node);
                break;
            case "label":
                validateLabel(node);
                break;
            case "rules":
                validateRules(node);
                break;
            case "rule":
                validateRule(node);
                break;
            case "event":
                validateEvent(node);
                break;
            case "subject":
                validateSubject(node);
                break;
            case "entity":
                validateEntity(node);
                break;
            case "process":
                validate(node);
                break;
            case "function":
                break;
            case "pc-spec":
                break;
            case "or":
                break;
            case "and":
                break;
            case "op-spec":
                break;
            case "op":
                break;
            case "target":
                break;
            case "container":
                break;
            case "time":
                break;
            case "dow":
                break;
            case "month":
                break;
            case "year":
                break;
            case "hour":
                break;
            case "start":
                break;
            case "end":
                break;
            case "response":
                break;
            case "condition":
                break;
            case "grant":
                break;
            case "create":
                break;
            case "deny":
                break;
            case "delete":
                break;

        }
    }

    private static void validateEntity(Node node) {
    }

    private static void validateSubject(Node node) {
    }

    private static void validateEvent(Node node) throws InvalidEvrException {
        List<String> childNodes = getChildNodes(node);
        checkInvalidElements(node.getNodeName(), childNodes);
    }

    /**
     * Allowed child tags: label, rules
     * Both are optional, so we just need to check if there are any invalid elements
     * @param node
     * @throws InvalidEvrException
     */
    private static void validateScript(Node node) throws InvalidEvrException {
        if(!node.getNodeName().equals(SCRIPT_TAG)) {
            throw new InvalidEvrException("script must be the root tag");
        }

        List<String> childNodes = getChildNodes(node);
        checkInvalidElements(node.getNodeName(), childNodes);
    }

    /**
     * Allowed child tags: N/A
     * There are no child tags allowed so we have to check that there are no child elements
     * @param node
     * @throws InvalidEvrException
     */
    private static void validateLabel(Node node) throws InvalidEvrException {
        List<String> childNodes = getChildNodes(node);
        checkInvalidElements(node.getNodeName(), childNodes);
    }

    /**
     * Allowed child tags: rule
     * Only rule elements are allowed and it is possible to have no elements.
     * @param node
     * @throws InvalidEvrException
     */
    private static void validateRules(Node node) throws InvalidEvrException {
        List<String> childNodes = getChildNodes(node);
        checkInvalidElements(node.getNodeName(), childNodes);
    }

    /**
     * Allowed child tags: event, response
     * A rule must have one event and one response element
     * @param node
     * @throws InvalidEvrException
     */
    private static void validateRule(Node node) throws InvalidEvrException {
        List<String> childNodes = getChildNodes(node);

        //check there are two elements
        if(childNodes.size() != 2) {
            throw new InvalidEvrException("Invalid number of elements.  Expected 2 elements got " + childNodes.size());
        }

        //check for invalid elements
        checkInvalidElements(node.getNodeName(), childNodes);

        //check for event and response
        boolean valid = childNodes.containsAll(Arrays.asList(EVENT_TAG, RESPONSE_TAG));
        if(!valid) {
            throw new InvalidEvrException("Missing event or response tag in rule");
        }
    }

    private static void checkInvalidElements(String nodeName, List<String> childNodes) throws InvalidEvrException{
        List<String> tags = allowedTags.get(nodeName);
        for(String child : childNodes) {
            if(tags.contains(child)) {
                throw new InvalidEvrException("Child element " + child + " is invalid for " + nodeName+ ". Valid elements are " + tags);
            }
        }
    }

    /*private void validateSubject(Node node) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(node);
        for(Node child : childNodes) {
            if(!subject.contains(child.getNodeName())) {
                throw new InvalidEvrException(child.getNodeName() + " is not valid in subject");
            }
        }
    }

    private void validateEntity(Node entityNode) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(entityNode);
        if(childNodes.size() > NUM_ENTITY_OPERANDS) {
            throw new InvalidEvrException("Invalid number of child tags for entity");
        }

        for(Node node : childNodes) {
            if(!entity.contains(node.getNodeName())) {
                throw new InvalidEvrException(node.getNodeName() + " is not valid in entity");
            }
        }
    }

    private void validateFunction(Node functionNode) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(functionNode);
        for(Node node : childNodes) {
            if(!function.contains(node.getNodeName())) {
                throw new InvalidEvrException(node.getNodeName() + " is not valid in function");
            }
        }
    }

    private void validateArg(Node argNode) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(argNode);
        if(childNodes.size() > NUM_ARG_OPERANDS) {
            throw new InvalidEvrException("Invalid number of child tags for arg");
        }

        for(Node node : childNodes) {
            if(!arg.contains(node.getNodeName())) {
                throw new InvalidEvrException(node.getNodeName() + " is not valid in arg");
            }
        }
    }

    private void validateProcess(Node entityNode) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(entityNode);
        if(childNodes.size() > NUM_PROCESS_OPERANDS) {
            throw new InvalidEvrException("Invalid number of child tags for process");
        }

        for(Node node : childNodes) {
            if(!process.contains(node.getNodeName())) {
                throw new InvalidEvrException(node.getNodeName() + " is not valid in process");
            }
        }
    }

    private void validatePcSpec(Node entityNode) throws InvalidEvrException {
        List<Node> childNodes = getChildNodes(entityNode);
        if(childNodes.size() > NUM_PROCESS_OPERANDS) {
            throw new InvalidEvrException("Invalid number of child tags for process");
        }

        for(Node node : childNodes) {
            if(!process.contains(node.getNodeName())) {
                throw new InvalidEvrException(node.getNodeName() + " is not valid in process");
            }
        }
    }*/



    private static List<String> getChildNodes(Node node) {
        List<String> nodes = new ArrayList<>();
        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); i++) {
            if(!childNodes.item(i).getNodeName().startsWith(IGNORE_CHAR)) {
                nodes.add(childNodes.item(i).getNodeName());
            }
        }

        return nodes;
    }

    class Tag {
        String name;
        List<TagGroup> tagGroups;

        public Tag(String name, List<TagGroup> tagGroups) {
            this.name = name;
            this.tagGroups = tagGroups;
        }

        public String getName() {
            return name;
        }

        public List<TagGroup> getTagGroups() {
            return tagGroups;
        }
    }

    class TagGroup {
        List<ChildTag> tags;

        public TagGroup(List<ChildTag> tags) {
            this.tags = tags;
        }

        public List<ChildTag> getTags() {
            return tags;
        }

        public List<String> getGroupNames() {
            List<String> names = new ArrayList<>();
            for(ChildTag tag : tags) {
                names.add(tag.getName());
            }

            return names;
        }
    }

    class ChildTag {
        String name;
        boolean required;
        int maxOccurances;

        public ChildTag(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        public ChildTag(String name, boolean required, int maxOccurances) {
            this.name = name;
            this.required = required;
            this.maxOccurances = maxOccurances;
        }

        public String getName() {
            return name;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
