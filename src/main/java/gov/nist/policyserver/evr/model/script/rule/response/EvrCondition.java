package gov.nist.policyserver.evr.model.script.rule.response;

import gov.nist.policyserver.evr.model.EvrEntity;

public class EvrCondition {
    private EvrEntity entity;
    private boolean exists;

    public EvrCondition() {

    }

    public EvrCondition(EvrEntity entity, boolean exists) {
        this.entity = entity;
        this.exists = exists;
    }

    public EvrEntity getEntity() {
        return entity;
    }

    public void setEntity(EvrEntity entity) {
        this.entity = entity;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
