package uk.ac.kcl.informatics.opmbuild;

import java.util.AbstractCollection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Represents an OPM graph, possibly still in the process of being completed.
 * As a data structure, a Graph is a Collection of Accounts, each of which contain the graph
 * elements in those accounts (where one element can be in multiple accounts).
 * A Graph starts with one special Account, called the <i>no account</i>, always present, which is the container for any
 * elements not in an (explicit) account.
 * A Graph can be annotated.
 * @author Simon Miles
 */
public class Graph extends AbstractCollection<Account> implements Annotated {
    private LinkedList<Account> _accounts;
    private AnnotationHandler _annotations;

    /**
     * Construct a graph with no elements.
     */
    public Graph () {
        _accounts = new LinkedList <Account> ();
        _annotations = new AnnotationHandler (this);
        add (new Account ());
    }

    /**
     * Add an account to the collection comprising this graph.
     * @param account The account to add
     * @return True if add was successful
     */
    @Override
    public boolean add (Account account) {
        return _accounts.add (account);
    }

    /**
     * Convenience method to add a graph element into the no account.
     * To avoid confusion, it is best to use this method only when not using accounts
     * (so treating the graph as one unbroken collection of graph elements).
     * @param elementInNoAccount The element to add to the graph/no account.
     */
    public void add (GraphElement elementInNoAccount) {
        noAccount ().add (elementInNoAccount);
    }

    /**
     * Adds the given annotation (as a key-value pair) to this graph.
     * @param key The key of the annotation to add
     * @param value The value of the annotation to add
     * @return This graph (to allow chaining of annotate calls)
     */
    public Annotated annotate (String key, Object value) {
        return _annotations.annotate (key, value);
    }

    /**
     * Adds the given annotation to this graph.
     * @param annotation The annotation to add
     * @return This graph (to allow chaining of annotate calls)
     */
    public Annotated annotate (Annotation annotation) {
        return _annotations.annotate (annotation);
    }

    /**
     * Tests whether this annotated object is equivalent to another in terms of
     * its annotations, i.e. whether the set of annotations on each is the same.
     * @param other The other annotated entity (most obviously another graph) to compare
     * @return True if this graph has the same annotations as the other annotated entity
     */
    public boolean equivalent (Annotated other) {
        return _annotations.equivalent (other);
    }

    /**
     * Returns the annotations to this graph.
     * @return The graph's annotations.
     */
    public Set<Annotation> getAnnotations () {
        return _annotations.getAnnotations ();
    }

    /**
     * Returns all graph elements in all accounts in this graph.
     * Note that this includes effects and causes of all edges in the graph,
     * regardless of whether they were independently added to an account.
     * @return The set of elements in this graph as a whole
     */
    public Set<GraphElement> getElements () {
        Set<GraphElement> all = new HashSet<GraphElement> ();
        Set<GraphElement> nodes = new HashSet<GraphElement> ();

        for (Account account : this) {
            all.addAll (account);
        }
        for (GraphElement element : all) {
            if (element instanceof Edge) {
                nodes.add (((Edge) element).getCause ());
                nodes.add (((Edge) element).getEffect ());
            }
        }
        all.addAll (nodes);

        return all;
    }

    /**
     * Returns true if the given account is the "no account" of this graph.
     * @param account An account to test
     * @return True if this is the no account
     */
    public boolean isNoAccount (Account account) {
        return account == noAccount ();
    }

    /**
     * Iterates over the accounts in the graph (including no account).
     * @return An iterator for the accounts in the graph.
     */
    @Override
    public Iterator<Account> iterator () {
        return _accounts.iterator ();
    }

    /**
     * Returns the set of graph elements not in any account. To add an element
     * with no account, simply add it to this set.
     * @return <i>The no account</i> for this graph
     */
    public Account noAccount () {
        return iterator ().next ();
    }

    /**
     * Convenience method to remove a graph element from the no account.
     * To avoid confusion, it is best to use this method only when not using accounts
     * (so treating the graph as one unbroken collection of graph elements).
     * @param elementInNoAccount The element to remove from the graph/no account.
     */
    public void remove (GraphElement elementInNoAccount) {
        noAccount ().remove (elementInNoAccount);
    }

    /**
     * Returns the number of accounts in this graph (remember that this count includes
     * the no account).
     * @return The number of accounts in the graph (including the no account).
     */
    @Override
    public int size () {
        return _accounts.size ();
    }
}
