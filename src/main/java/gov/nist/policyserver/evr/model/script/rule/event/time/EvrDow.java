package gov.nist.policyserver.evr.model.script.rule.event.time;

import java.util.List;

public class EvrDow {
    private List<Integer> dow;
    private EvrTimeRange range;

    public EvrDow(List<Integer> dow) {
        this.dow = dow;
    }

    public EvrDow(int start, int end) {
        this.range = new EvrTimeRange(start, end);
    }

    public List<Integer> getDow() {
        return dow;
    }

    public void addDow(int dow) {
        this.dow.add(dow);
    }

    public EvrTimeRange getRange() {
        return range;
    }
}
