package uk.ac.kcl.informatics.opmbuild.query.memory;

import java.util.Collection;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.Query;

public abstract class InMemoryQuery extends Query {
    public abstract Collection<Object> perform (Graph graph, Collection<Object> inputs);
}
