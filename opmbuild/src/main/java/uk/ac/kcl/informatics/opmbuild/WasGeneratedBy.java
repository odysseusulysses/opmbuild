package uk.ac.kcl.informatics.opmbuild;

public class WasGeneratedBy extends RoleAnnotatedEdge {

    public WasGeneratedBy () {
        this (null, null);
    }

    public WasGeneratedBy (Artifact effect, Process cause) {
        super (effect, cause);
    }

    public WasGeneratedBy (Artifact effect, String effectRole, Process cause) {
        super (effect, effectRole, cause);
    }

    public WasGeneratedBy (Artifact effect, Process cause, Time time) {
        super (effect, cause, time);
    }

    public WasGeneratedBy (Artifact effect, String effectRole, Process cause, Time time) {
        super (effect, effectRole, cause, time);
    }

    @Override
    public Process getCause () {
        return (Process) super.getCause ();
    }

    @Override
    public Artifact getEffect () {
        return (Artifact) super.getEffect ();
    }
}
