package uk.ac.kcl.informatics.opmbuild;

import java.util.HashSet;
import java.util.Set;

class AnnotationHandler implements Annotated {
    private Set<Annotation> _annotations;
    private Annotated _annotated;

    AnnotationHandler () {
        this (null);
        _annotated = this;
    }

    AnnotationHandler (Annotated annotated) {
        _annotated = annotated;
        _annotations = new HashSet<Annotation> ();
    }

    public Annotated annotate (String key, Object value) {
        return annotate (new Annotation (key, value));
    }

    public Annotated annotate (Annotation annotation) {
        _annotations.add (annotation);
        return _annotated;
    }

    /**
     * Tests whether this node has the same annotations as another node
     * @param other Node to be compared to
     * @return True if nodes have same annotations
     */
    public boolean equivalent (Annotated other) {
        return other.getAnnotations ().equals (getAnnotations ());
    }

    public Set<Annotation> getAnnotations () {
        return _annotations;
    }
}
