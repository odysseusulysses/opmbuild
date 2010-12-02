package uk.ac.kcl.informatics.opmbuild;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

public class Retrieve {

    public static Set<Account> getAccountsContaining (Graph graph, GraphElement element) {
        Set<Account> contains = new HashSet<Account> ();

        for (Account account : graph) {
            if (account.contains (element)) {
                contains.add (account);
            }
        }

        return contains;
    }

    public static Annotation getAnnotationByKey (Annotated entity, String key) {
        try {
            return getAnnotationsByKey (entity, key).iterator ().next ();
        } catch (NoSuchElementException noneExists) {
            return null;
        }
    }

    public static Set<Annotation> getAnnotationsByKey (Annotated entity, String key) {
        Set<Annotation> values = new HashSet<Annotation> ();

        for (Annotation annotation : entity.getAnnotations ()) {
            if (annotation.getKey ().equals (key)) {
                values.add (annotation);
            }
        }

        return values;
    }

    public static Set<Node> getCauses (Collection<GraphElement> elements) {
        Set<Node> causes = new HashSet<Node> ();

        for (GraphElement element : elements) {
            if (element instanceof Edge) {
                causes.add (((Edge) element).getCause ());
            }
        }

        return causes;
    }

    public static Set<Edge> getEdges (Collection<GraphElement> elements) {
        Set<Edge> edges = new HashSet<Edge> ();

        for (GraphElement element : elements) {
            if (element instanceof Edge) {
                edges.add ((Edge) element);
            }
        }

        return edges;
    }

    public static Set<Node> getEffects (Collection<GraphElement> elements) {
        Set<Node> causes = new HashSet<Node> ();

        for (GraphElement element : elements) {
            if (element instanceof Edge) {
                causes.add (((Edge) element).getCause ());
            }
        }

        return causes;
    }

    public static Set<Node> getNodes (Collection<GraphElement> elements) {
        Set<Node> nodes = new HashSet<Node> ();

        for (GraphElement element : elements) {
            if (element instanceof Node) {
                nodes.add ((Node) element);
            }
        }

        return nodes;
    }

    public static Set<Object> getValues (Collection<Annotation> annotations) {
        Set<Object> values = new HashSet<Object> ();

        for (Annotation annotation : annotations) {
            values.add (annotation.getValue ());
        }

        return values;
    }

    public static Object getValueByKey (Annotated entity, String key) {
        try {
            return getValuesByKey (entity, key).iterator ().next ();
        } catch (NoSuchElementException noneExists) {
            return null;
        }
    }

    public static Set<Object> getValuesByKey (Annotated entity, String key) {
        return getValues (getAnnotationsByKey (entity, key));
    }

    public static boolean hasAnnotation (Annotated annotated, String key, Object value) {
        for (Annotation note : annotated.getAnnotations ()) {
            if (note.getKey ().equals (key) && note.getValue ().equals (value)) {
                return true;
            }
        }
        return false;
    }

    public static void removeAnnotationsByKey (Annotated entity, String key) {
        Set<Annotation> toRemove = new HashSet<Annotation> ();

        for (Annotation annotation : entity.getAnnotations ()) {
            if (annotation.getKey ().equals (key)) {
                toRemove.add (annotation);
            }
        }
        entity.getAnnotations ().removeAll (toRemove);
    }
}
