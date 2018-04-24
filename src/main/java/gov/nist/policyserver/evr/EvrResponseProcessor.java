package gov.nist.policyserver.evr;

import gov.nist.policyserver.evr.model.EvrRule;
import gov.nist.policyserver.evr.model.script.rule.response.EvrAction;
import gov.nist.policyserver.evr.model.script.rule.response.EvrCondition;
import gov.nist.policyserver.evr.model.script.rule.response.EvrResponse;

import java.util.List;

public class EvrResponseProcessor {

    public void processResponse(EvrResponse response) {
        if(processCondition(response.getCondition())) {
            List<EvrAction> actions = response.getActions();
            for(EvrAction action : actions) {
                doAction(action);
            }
        }
    }

    private boolean processCondition(EvrCondition condition) {
        return false;
    }

    public void doAction(EvrAction action) {
        System.out.println("doing action ...");
    }
}
