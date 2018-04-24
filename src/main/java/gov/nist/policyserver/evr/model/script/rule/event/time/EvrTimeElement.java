package gov.nist.policyserver.evr.model.script.rule.event.time;

import java.util.ArrayList;
import java.util.List;

public class EvrTimeElement {
    private List<Integer> values;
    private EvrTimeRange  range;

    public EvrTimeElement() {
        values = new ArrayList<>();
    }

    public EvrTimeElement(List<Integer> values) {
        this.values = values;
    }

    public EvrTimeElement(int start, int end) {
        this.range = new EvrTimeRange(start, end);
    }

    public List<Integer> getValues() {
        return values;
    }

    public void addValue(int value) {
        this.values.add(value);
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public EvrTimeRange getRange() {
        return range;
    }

    public boolean isRange() {
        return range != null;
    }

    public void setRange(int start, int end) {
        this.range = new EvrTimeRange(start, end);
    }

    public boolean equals(Object o) {
        if(!(o instanceof EvrTimeElement)) {
            return false;
        }

        EvrTimeElement element = (EvrTimeElement) o;

        if(this.isRange() == element.isRange()) {

        } else {
            return false;
        }

        return false;
    }
}
