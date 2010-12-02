package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.HashSet;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Used;
import uk.ac.kcl.informatics.opmbuild.WasGeneratedBy;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class HasRole extends InMemoryQuery {
    private String _role;

    public HasRole (String role) {
        _role = role;
    }

    public String getRole () {
        return _role;
    }

    @Override
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Collection<Object> results = new HashSet <Object> ();

        for (Object input : inputs) {
            if (input instanceof Used && ((Used) input).getRole ().equals (_role)) {
                results.add (input);
            }
            if (input instanceof WasGeneratedBy && ((WasGeneratedBy) input).getRole ().equals (_role)) {
                results.add (input);
            }
        }

        return results;
    }
}
