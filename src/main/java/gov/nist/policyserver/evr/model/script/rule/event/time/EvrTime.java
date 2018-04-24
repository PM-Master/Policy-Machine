package gov.nist.policyserver.evr.model.script.rule.event.time;

public class EvrTime {
    private EvrTimeElement dow;
    private EvrTimeElement day;
    private EvrTimeElement month;
    private EvrTimeElement year;
    private EvrTimeElement hour;

    public EvrTimeElement getDow() {
        return dow;
    }

    public void setDow(EvrTimeElement dow) {
        this.dow = dow;
    }

    public EvrTimeElement getDay() {
        return day;
    }

    public void setDay(EvrTimeElement day) {
        this.day = day;
    }

    public EvrTimeElement getMonth() {
        return month;
    }

    public void setMonth(EvrTimeElement month) {
        this.month = month;
    }

    public EvrTimeElement getYear() {
        return year;
    }

    public void setYear(EvrTimeElement year) {
        this.year = year;
    }

    public EvrTimeElement getHour() {
        return hour;
    }

    public void setHour(EvrTimeElement hour) {
        this.hour = hour;
    }

    public boolean equals(Object o) {
        if(!(o instanceof EvrTime)) {
            return false;
        }

        EvrTime evrTime = (EvrTime) o;

        return this.getDow().equals(evrTime.getDow()) &&
                this.getDay().equals(evrTime.getDay()) &&
                this.getMonth().equals(evrTime.getMonth()) &&
                this.getYear().equals(evrTime.getYear()) &&
                this.getHour().equals(evrTime.getHour());

    }
}
