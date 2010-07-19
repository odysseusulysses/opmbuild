package uk.ac.kcl.informatics.opmbuild.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.GraphElement;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryExecutor;

public class QueryUtils {
    public static Graph executeToGraph (Query query, Graph supergraph) {
        InMemoryExecutor.execute (query, supergraph);
        return resultsToGraph (getStoredResults (query));
    }

    public static Collection<Query> getSteps (Query query) {
        return getSteps (query, new LinkedList<Query> ());
    }

    private static Collection<Query> getSteps (Query query, Collection<Query> found) {
        if (!found.contains (query)) {
            found.add (query);
            for (Query child : query.getChildren ()) {
                getSteps (child, found);
            }
        }
        
        return found;
    }

    public static Set<Object> getStoredResults (Query query) {
        Set<Object> results = new HashSet <Object> ();

        for (Query step : getSteps (query)) {
            if (step instanceof MemoryStore) {
                results.addAll (((MemoryStore) step).getResults ());
            }
        }

        return results;
    }

    public static Graph resultsToGraph (Set<Object> results) {
        Graph graph = new Graph ();

        for (Object result : results) {
            if (result instanceof GraphElement) {
                graph.noAccount ().add ((GraphElement) result);
            }
        }

        return graph;
    }
}
