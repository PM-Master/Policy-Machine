package gov.nist.policyserver.evr.model.script.rule;

import gov.nist.policyserver.evr.model.script.rule.event.Event;
import gov.nist.policyserver.evr.model.script.rule.response.Response;

public class Rule {
    private Event    event;
    private Response response;

    public Rule(Event event, Response response) {
        this.event = event;
        this.response = response;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
