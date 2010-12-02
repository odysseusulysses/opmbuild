package uk.ac.kcl.informatics.opmbuild;

import java.util.Date;

public class Time {
    private Date _noEarlierThan;
    private Date _noLaterThan;

    public Time (Date noEarlierThan, Date noLaterThan) {
        _noEarlierThan = noEarlierThan;
        _noLaterThan = noLaterThan;
    }
    
    public Time (Date instant) {
        this (instant, instant);
    }

    public Date getNoEarlierThan () {
        return _noEarlierThan;
    }

    public Date getNoLaterThan () {
        return _noLaterThan;
    }
}
