package uk.ac.kcl.informatics.opmbuild;

public abstract class RoleAnnotatedEdge extends TimeAnnotatedEdge {
    private String _role;

    public RoleAnnotatedEdge (Node effect, Node cause) {
        super (effect, cause);
    }

    public RoleAnnotatedEdge (Node effect, String role, Node cause) {
        super (effect, cause);
        setRole (role);
    }

    public RoleAnnotatedEdge (Node effect, Node cause, Time time) {
        super (effect, cause, time);
    }

    public RoleAnnotatedEdge (Node effect, String role, Node cause, Time time) {
        super (effect, cause, time);
        setRole (role);
    }

    public String getRole () {
        return _role;
    }

    public void setRole (String value) {
        _role = value;
    }
}
