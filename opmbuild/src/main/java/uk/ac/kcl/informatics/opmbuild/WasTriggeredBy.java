package uk.ac.kcl.informatics.opmbuild;

public class WasTriggeredBy extends TimeAnnotatedEdge {
    public WasTriggeredBy () {
        this (null, null);
    }

    public WasTriggeredBy (Process effect, Process cause) {
        super (effect, cause);
    }

    public WasTriggeredBy (Process effect, Process cause, Time time) {
        super (effect, cause, time);
    }

    @Override
    public Process getCause () {
        return (Process) super.getCause ();
    }

    @Override
    public Process getEffect () {
        return (Process) super.getEffect ();
    }
}
