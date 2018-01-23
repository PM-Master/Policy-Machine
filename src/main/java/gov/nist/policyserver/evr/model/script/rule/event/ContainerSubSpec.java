package gov.nist.policyserver.evr.model.script.rule.event;

import gov.nist.policyserver.model.graph.nodes.Node;

import java.util.ArrayList;
import java.util.List;

public class ContainerSubSpec {
    List<Node> objects;
    List<String> classes;
    List<Node> oattrs;

    public ContainerSubSpec() {
        this.objects = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.oattrs = new ArrayList<>();
    }

    public List<Node> getObjects() {
        return objects;
    }

    public void setObjects(List<Node> objects) {
        this.objects = objects;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<Node> getOattrs() {
        return oattrs;
    }

    public void setOattrs(List<Node> oattrs) {
        this.oattrs = oattrs;
    }
}
