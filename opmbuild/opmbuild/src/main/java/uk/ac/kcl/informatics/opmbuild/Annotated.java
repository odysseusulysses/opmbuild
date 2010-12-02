package uk.ac.kcl.informatics.opmbuild;

import java.util.Set;

public interface Annotated {
    Annotated annotate (String key, Object value);
    Annotated annotate (Annotation annotation);

    /**
     * Tests whether this has the same annotations as another annotated entity
     * @param other Node to be compared to
     * @return True if nodes have same annotations
     */
    boolean equivalent (Annotated other);
    Set<Annotation> getAnnotations ();
}
