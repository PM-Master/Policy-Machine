package gov.nist.policyserver.evr.model.script.rule.event.time;

public class EvrTimeRange {
    private int start;
    private int end;

    public EvrTimeRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
