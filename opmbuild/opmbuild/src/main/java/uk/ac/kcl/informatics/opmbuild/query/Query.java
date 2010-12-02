package uk.ac.kcl.informatics.opmbuild.query;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

/**
 * A query is a workflow, which includes steps.
 * The root step is given all elements in the graph as input.
 * Each step passes its results to one or more others.
 * A query step is called only if its input set is non-empty.
 * The query ends when there are no more workflow steps which can be called.
 */
public class Query {
    private Set<Query> _children;
    private String _name;

    public Query () {
        _children = new HashSet<Query> ();
        _name = "";
    }

    public Query addChild (Query child) {
        _children.add (child);
        return this;
    }

    public Query asOneStep () {
        return new EncapsulatedQuery (this);
    }

    public Set<Query> getChildren () {
        return _children;
    }

    public String getName () {
        return _name;
    }

    public Query setName (String name) {
        _name = name;
        return this;
    }
}
