package uk.ac.kcl.informatics.opmbuild.format;

import java.util.LinkedList;
import java.util.List;
import uk.ac.kcl.informatics.opmbuild.Account;
import uk.ac.kcl.informatics.opmbuild.Annotated;
import uk.ac.kcl.informatics.opmbuild.Annotation;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.Retrieve;

/**
 * Generates identifiers for graph components suitable for serialisation, i.e.
 * sequences of digits and letters starting with a letter.
 * @author Simon
 */
public class GraphSerialisationIDs {
    private List<Account> _accounts;
    private List<Node> _nodes;
    private List<Edge> _edges;
    private List<String> _keys;

    public GraphSerialisationIDs (Graph graph) {
        _accounts = new LinkedList<Account> (graph);
        _nodes = new LinkedList<Node> (Retrieve.getNodes (graph.getElements ()));
        _edges = new LinkedList<Edge> (Retrieve.getEdges (graph.getElements ()));
        _keys = new LinkedList<String> ();
    }

    public String accountID (Account account) {
        return getID (account, _accounts, "ACCT");
    }

    public String annotationID (Annotated annotated, Annotation note) {
        if (annotated instanceof Edge) {
            return "ANN" + keyID (note.getKey ()) + "for" + edgeID ((Edge) annotated);
        }
        if (annotated instanceof Node) {
            return "ANN" + keyID (note.getKey ()) + "for" + nodeID ((Node) annotated);
        }
        return "ANN" + keyID (note.getKey ());
    }

    public String edgeID (Edge edge) {
        return getID (edge, _edges, "EDGE");
    }

    private <T> String getID (T ided, List<T> list, String prefix) {
        for (int index = 0; index < list.size (); index += 1) {
            if (list.get (index) == ided) {
                return prefix + index;
            }
        }
        return null;
    }

    private int keyID (String key) {
        if (_keys.contains (key)) {
            return _keys.indexOf (key);
        } else {
            _keys.add (key);
            return _keys.size () - 1;
        }
    }

    public String nodeID (Node node) {
        return getID (node, _nodes, "NODE");
    }
}
