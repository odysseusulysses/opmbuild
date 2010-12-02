package uk.ac.kcl.informatics.opmbuild;

public class WasControlledBy extends RoleAnnotatedEdge {

    private String _role;

    public WasControlledBy () {
        this (null, null);
    }

    public WasControlledBy (Process effect, Agent cause) {
        super (effect, cause);
    }

    public WasControlledBy (Process effect, String causeRole, Agent cause) {
        super (effect, causeRole, cause);
    }

    public WasControlledBy (Process effect, Agent cause, Time time) {
        super (effect, cause, time);
    }

    public WasControlledBy (Process effect, String causeRole, Agent cause, Time time) {
        super (effect, causeRole, cause, time);
    }

    @Override
    public Agent getCause () {
        return (Agent) super.getCause ();
    }

    @Override
    public Process getEffect () {
        return (Process) super.getEffect ();
    }
}
