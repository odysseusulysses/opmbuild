package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Annotated;
import uk.ac.kcl.informatics.opmbuild.Annotation;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Retrieve;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class HasAnnotation extends InMemoryQuery {
    private String _key;
    private Object _value;
    
    public HasAnnotation (String key, Object value) {
        _key = key;
        _value = value;
    }

    public HasAnnotation (Annotation annotation) {
        this (annotation.getKey (), annotation.getValue ());
    }
    
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Set<Object> filtered = new HashSet <Object> ();
        
        for (Object input : inputs) {
            if (input instanceof Annotated) {
                if (Retrieve.hasAnnotation ((Annotated) input, _key, _value)) {
                    filtered.add (input);
                }
            }
        }

        return filtered;
    }

    public String getKey () {
        return _key;
    }

    public Object getValue () {
        return _value;
    }
}
