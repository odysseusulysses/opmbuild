package uk.ac.kcl.informatics.opmbuild.query.memory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.Query;
import uk.ac.kcl.informatics.opmbuild.query.QueryUtils;

public class InMemoryExecutor {

    public static Set<Object> execute (Query query, Graph graph) {
        return execute (query, graph, new HashSet<Object> (graph.getElements ()));
    }

    public static Set<Object> execute (Query query, Graph graph, Collection<Object> start) {
        executeQuery (query, graph, start);

        return QueryUtils.getStoredResults (query);
    }

    private static void executeQuery (Query query, Graph graph, Collection<Object> current) {
        InMemoryQuery root = (InMemoryQuery) query;
        Collection<Object> next = root.perform (graph, current);

        if (!next.isEmpty ()) {
            for (Query subquery : query.getChildren ()) {
                executeQuery (subquery, graph, next);
            }
        }
    }
}
