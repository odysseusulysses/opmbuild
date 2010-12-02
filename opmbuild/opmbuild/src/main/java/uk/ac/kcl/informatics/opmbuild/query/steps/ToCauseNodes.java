package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class ToCauseNodes extends InMemoryQuery {
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Set<Object> next = new HashSet<Object> ();

        for (Object input : inputs) {
            if (input instanceof Edge) {
                next.add (((Edge) input).getCause ());
            }
        }

        return next;
    }
}
