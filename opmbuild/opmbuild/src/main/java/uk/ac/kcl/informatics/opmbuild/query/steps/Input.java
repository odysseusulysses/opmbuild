package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.Collections;
import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class Input extends InMemoryQuery {

    private Artifact _input;

    public Input (Artifact input) {
        _input = input;
    }

    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        return Collections.singleton ((Object) _input);
    }

    public Artifact getInput () {
        return _input;
    }
}
