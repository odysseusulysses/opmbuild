package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.Retrieve;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class ToEdgesToEffects extends InMemoryQuery {
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Set<Object> next = new HashSet<Object> ();
        Set<Edge> edges = Retrieve.getEdges (graph.getElements ());

        for (Object input : inputs) {
            if (input instanceof Node) {
                for (Edge edge : edges) {
                    next.add (edge);
                }
            }
        }

        return next;
    }
}
