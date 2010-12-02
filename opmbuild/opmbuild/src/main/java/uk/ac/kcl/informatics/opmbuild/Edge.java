package uk.ac.kcl.informatics.opmbuild;

public abstract class Edge extends AnnotationHandler implements GraphElement {
    private Node _effect;
    private Node _cause;
    
    Edge (Node effect, Node cause) {
        _effect = effect;
        _cause = cause;
    }

    public Node getCause () {
        return _cause;
    }

    public Node getEffect () {
        return _effect;
    }

    public void setCause (Node cause) {
        _cause = cause;
    }

    public void setEffect (Node effect) {
        _effect = effect;
    }
}
