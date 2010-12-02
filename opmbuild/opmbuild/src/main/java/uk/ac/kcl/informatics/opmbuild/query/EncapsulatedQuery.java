package uk.ac.kcl.informatics.opmbuild.query;

import java.util.Collection;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryExecutor;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class EncapsulatedQuery extends InMemoryQuery {

    private Query _root;

    public EncapsulatedQuery (Query root) {
        _root = root;
    }

    public Query getRoot () {
        return _root;
    }

    @Override
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        return InMemoryExecutor.execute (_root, graph, inputs);
    }
}
