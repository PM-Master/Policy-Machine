package gov.nist.policyserver.evr.model.script;

import gov.nist.policyserver.evr.model.script.rule.Rule;

import java.util.ArrayList;
import java.util.List;

public class Script {
    private String     scriptName;
    private List<Rule> rules;

    public Script(String scriptName) {
        this.scriptName = scriptName;
        this.rules = new ArrayList<>();
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public void addRule(Rule rule) {
        this.rules.add(rule);
    }
}
