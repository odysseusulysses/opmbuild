package uk.ac.kcl.informatics.opmbuild;

public class Used extends RoleAnnotatedEdge {
    public Used () {
        this (null, null);
    }

    public Used (Process effect, Artifact cause) {
        super (effect, cause);
    }

    public Used (Process effect, String causeRole, Artifact cause) {
        super (effect, causeRole, cause);
    }

    public Used (Process effect, Artifact cause, Time time) {
        super (effect, cause, time);
    }

    public Used (Process effect, String causeRole, Artifact cause, Time time) {
        super (effect, causeRole, cause, time);
    }

    @Override
    public Artifact getCause () {
        return (Artifact) super.getCause ();
    }

    @Override
    public Process getEffect () {
        return (Process) super.getEffect ();
    }
}
