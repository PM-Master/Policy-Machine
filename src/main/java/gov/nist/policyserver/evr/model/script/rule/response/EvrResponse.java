package gov.nist.policyserver.evr.model.script.rule.response;

import java.util.ArrayList;
import java.util.List;

public class EvrResponse {
    EvrCondition    condition;
    List<EvrAction> actions;

    public EvrResponse() {
        actions = new ArrayList<>();
    }

    public EvrCondition getCondition() {
        return condition;
    }

    public void setCondition(EvrCondition condition) {
        this.condition = condition;
    }

    public List<EvrAction> getActions() {
        return actions;
    }

    public void setActions(List<EvrAction> actions) {
        this.actions = actions;
    }

    public void addAction(EvrAction action) {
        this.actions.add(action);
    }
}
