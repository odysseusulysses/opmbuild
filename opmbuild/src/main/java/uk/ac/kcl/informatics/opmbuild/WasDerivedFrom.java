package uk.ac.kcl.informatics.opmbuild;

public class WasDerivedFrom extends TimeAnnotatedEdge {
    public WasDerivedFrom () {
        this (null, null);
    }

    public WasDerivedFrom (Artifact effect, Artifact cause) {
        super (effect, cause);
    }

    public WasDerivedFrom (Artifact effect, Artifact cause, Time time) {
        super (effect, cause, time);
    }

    @Override
    public Artifact getCause () {
        return (Artifact) super.getCause ();
    }

    @Override
    public Artifact getEffect () {
        return (Artifact) super.getEffect ();
    }
}
