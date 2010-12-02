package uk.ac.kcl.informatics.opmbuild.query;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class MemoryStore extends InMemoryQuery {
    private Set<Object> _results;

    public MemoryStore () {
        _results = new HashSet<Object> ();
    }

    public Set<Object> getResults () {
        return _results;
    }

    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        _results.addAll (inputs);
        return Collections.EMPTY_SET;
    }

}
