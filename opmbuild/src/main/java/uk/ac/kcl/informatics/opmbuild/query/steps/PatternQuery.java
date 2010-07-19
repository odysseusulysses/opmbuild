package uk.ac.kcl.informatics.opmbuild.query.steps;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Account;
import uk.ac.kcl.informatics.opmbuild.Annotated;
import uk.ac.kcl.informatics.opmbuild.Annotation;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.Retrieve;
import uk.ac.kcl.informatics.opmbuild.query.memory.InMemoryQuery;

public class PatternQuery extends InMemoryQuery {

    private Account _pattern;

    public PatternQuery (Account pattern) {
        _pattern = pattern;
    }

    @Override
    public Collection<Object> perform (Graph graph, Collection<Object> inputs) {
        Map<Node, Set<Node>> nodeMap = new HashMap<Node, Set<Node>> ();
        Map<Edge, Set<Edge>> edgeMap = new HashMap<Edge, Set<Edge>> ();

        for (Node node : Retrieve.getNodes (_pattern)) {
            findMatches (node, inputs, nodeMap);
        }
        for (Edge edge : Retrieve.getEdges (_pattern)) {
            findMatches (edge, inputs, nodeMap, edgeMap);
        }

        return findSolutions (nodeMap, edgeMap);
    }

    private void findMatches (Node node, Collection<Object> inputs, Map<Node, Set<Node>> matches) {
        for (Object input : inputs) {
            if (input instanceof Node) {
                if (matchesAnnotations (node, ((Node) input))) {
                    addMatch (matches, node, (Node) input);
                }
            }
        }
    }

    private boolean matchesAnnotations (Annotated pattern, Annotated input) {
        boolean found;

        for (Annotation note : pattern.getAnnotations ()) {
            found = false;
            for (Annotation existing : input.getAnnotations ()) {
                if (existing.getKey ().equals (note.getKey ())) {
                    if (note.getValue () instanceof VariablePatternValue
                            || note.getValue ().equals (existing.getValue ())) {
                        found = true;
                    }
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    private <T> void addMatch (Map<T, Set<T>> matches, T patternPart, T inputPart) {
        Set<T> existing = matches.get (patternPart);

        if (existing == null) {
            existing = new HashSet<T> ();
            matches.put (patternPart, existing);
        }
        existing.add (inputPart);
    }

    private void findMatches (Edge edge, Collection<Object> inputs, Map<Node, Set<Node>> nodeMap, Map<Edge, Set<Edge>> edgeMap) {
        for (Object input : inputs) {
            if (input instanceof Edge) {
                if (matchesAnnotations (edge, ((Edge) input))) {
                    if (nodeMap.get (edge.getEffect ()).contains (((Edge) input).getEffect ())
                            && nodeMap.get (edge.getCause ()).contains (((Edge) input).getCause ())) {
                        addMatch (edgeMap, edge, (Edge) input);
                    }
                }
            }
        }
    }

    private Set<Object> findSolutions (Map<Node, Set<Node>> nodeMap, Map<Edge, Set<Edge>> edgeMap) {
        Set<Object> solutions = new HashSet<Object> ();
        Set<Map<Object, Object>> maps = new HashSet<Map<Object, Object>> ();
        Set<Map<Object, Object>> next;
        Map<Object, Object> merged;
        Account solution;

        for (Edge patternEdge : edgeMap.keySet ()) {
            next = new HashSet<Map<Object, Object>> ();
            for (Edge matchEdge : edgeMap.get (patternEdge)) {
                for (Map<Object, Object> map : maps) {
                    if (isCompatible (map, patternEdge, matchEdge)) {
                        merged = new HashMap<Object, Object> (map);
                        merged.put (patternEdge, matchEdge);
                        merged.putAll (bindAllVariables (patternEdge, matchEdge));
                        next.add (merged);
                    }
                }
            }
            maps = next;
        }
        for (Map<Object, Object> map : maps) {
            solution = new Account ();
            for (Object match : map.values ()) {
                if (match instanceof Edge) {
                    solution.add ((Edge) match);
                    solution.add (((Edge) match).getEffect ());
                    solution.add (((Edge) match).getCause ());
                }
            }
            solutions.add (solution);
        }

        return solutions;
    }

    private boolean isCompatible (Map<Object, Object> solution, Edge patternEdge, Edge matchEdge) {
        Node effect = patternEdge.getEffect ();
        Node cause = patternEdge.getCause ();

        for (Object part : solution.keySet ()) {
            if (part instanceof Edge) {
                if (((Edge) part).getEffect ().equals (effect)
                        && !((Edge) solution.get (part)).getEffect ().equals (matchEdge.getEffect ())) {
                    return false;
                }
                if (((Edge) part).getCause ().equals (cause)
                        && !((Edge) solution.get (part)).getCause ().equals (matchEdge.getCause ())) {
                    return false;
                }
                if (!doBindingsMatch (solution, patternEdge, matchEdge)
                        || !doBindingsMatch (solution, patternEdge.getEffect (), matchEdge.getEffect ())
                        || !doBindingsMatch (solution, patternEdge.getCause (), matchEdge.getCause ())) {
                    return false;
                }
            }
        }

        return true;
    }

    private Map<Object, Object> bindAllVariables (Edge patternEdge, Edge matchEdge) {
        Map<Object, Object> binding = new HashMap<Object, Object> ();

        binding.putAll (bindVariables (patternEdge, matchEdge));
        binding.putAll (bindVariables (patternEdge.getEffect (), matchEdge.getEffect ()));
        binding.putAll (bindVariables (patternEdge.getCause (), matchEdge.getCause ()));

        return binding;
    }

    private Map<Object, Object> bindVariables (Annotated pattern, Annotated match) {
        Map<Object, Object> binding = new HashMap<Object, Object> ();

        for (Annotation note : pattern.getAnnotations ()) {
            if (note.getValue () instanceof VariablePatternValue) {
                binding.put (note.getValue (), Retrieve.getValueByKey (match, note.getKey ()));
            }
        }

        return binding;
    }

    private boolean doBindingsMatch (Map<Object, Object> solution, Annotated pattern, Annotated match) {
        for (Annotation note : pattern.getAnnotations ()) {
            if (note.getValue () instanceof VariablePatternValue
                    && solution.get (note.getValue ()) != null
                    && !solution.get (note.getValue ()).equals (Retrieve.getValueByKey (match, note.getKey ()))) {
                return false;
            }
        }
        return true;
    }
}
