package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import uk.ac.kcl.informatics.opmbuild.Account;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class WholeAccount extends InMemoryQuery {
    @Override
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Collection<Object> outputs = new HashSet<Object> ();

        for (Account account : graph) {
            if (!Collections.disjoint (account, inputs)) {
                outputs.addAll (account);
            }
        }

        return outputs;
    }
}
