package gov.nist.policyserver.evr.model.script.rule.event;

public class ObjSpec {
    ObjSubSpec objSubSpec;
    ContainerSubSpec containerSubSpec;

    public ObjSpec() {
        objSubSpec = new ObjSubSpec();
        containerSubSpec = new ContainerSubSpec();
    }

    public ObjSubSpec getObjSubSpec() {
        return objSubSpec;
    }

    public void setObjSubSpec(ObjSubSpec objSubSpec) {
        this.objSubSpec = objSubSpec;
    }

    public ContainerSubSpec getContainerSubSpec() {
        return containerSubSpec;
    }

    public void setContainerSubSpec(ContainerSubSpec containerSubSpec) {
        this.containerSubSpec = containerSubSpec;
    }
}
