package uk.ac.kcl.informatics.opmbuild.format.opmv.v10;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Account;
import uk.ac.kcl.informatics.opmbuild.Agent;
import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.GraphElement;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.Process;
import uk.ac.kcl.informatics.opmbuild.RoleAnnotatedEdge;
import uk.ac.kcl.informatics.opmbuild.Time;
import uk.ac.kcl.informatics.opmbuild.TimeAnnotatedEdge;
import uk.ac.kcl.informatics.opmbuild.Used;
import uk.ac.kcl.informatics.opmbuild.WasControlledBy;
import uk.ac.kcl.informatics.opmbuild.WasDerivedFrom;
import uk.ac.kcl.informatics.opmbuild.WasGeneratedBy;
import uk.ac.kcl.informatics.opmbuild.WasTriggeredBy;
import uk.ac.kcl.informatics.opmbuild.format.CommonSerialiser;
import uk.ac.kcl.informatics.opmbuild.format.GraphSerialisationIDs;

public class OPMV10TurtleSerialiser extends CommonSerialiser {

    private static final String OPMV_AGENT = "<http://purl.org/net/opmv/ns#Agent>";
    private static final String OPMV_ARTIFACT = "<http://purl.org/net/opmv/ns#Artifact>";
    private static final String OPMV_PROCESS = "<http://purl.org/net/opmv/ns#Process>";
    private static final String OPMV_USED = "<http://purl.org/net/opmv/ns#used>";
    private static final String OPMV_WASCONTROLLEDBY = "<http://purl.org/net/opmv/ns#wasControlledBy>";
    private static final String OPMV_WASGENERATEDBY = "<http://purl.org/net/opmv/ns#wasGeneratedBy>";
    private static final String OPMV_WASDERIVEDFROM = "<http://purl.org/net/opmv/ns#wasDerivedFrom>";
    private static final String OPMV_WASTRIGGEREDBY = "<http://purl.org/net/opmv/ns#wasTriggeredBy>";
    private static final String OPMV_WASGENERATEDAT = "<http://purl.org/net/opmv/ns#wasGeneratedAt>";
    private static final String OPMV_WASUSEDAT = "<http://purl.org/net/opmv/ns#wasUsedAt>";
    private static final String TIME_INSTANT = "<http://www.w3.org/2006/time#Instant>";
    private static final String TIME_INXSD = "<http://www.w3.org/2006/time#inXSDDateTime>";
    private String _namespace;

    public OPMV10TurtleSerialiser (String graphsNamespace) {
        setGraphNamespace (graphsNamespace);
    }

    private Map<String, String> extractNamespaces (Set<Triple> triples) {
        Map<String, String> prefixes = new HashMap<String, String> ();

        for (Triple triple : triples) {
            extractNamespace (triple, prefixes);
        }

        return prefixes;
    }

    private void extractNamespace (Triple triple, Map<String, String> prefixes) {
        triple._subject = extractNamespace (triple._subject, prefixes);
        triple._predicate = extractNamespace (triple._predicate, prefixes);
        triple._object = extractNamespace (triple._object, prefixes);
    }

    private String extractNamespace (String value, Map<String, String> prefixes) {
        int hash;
        String namespace, prefix;

        if (value.startsWith ("<")) {
            hash = value.indexOf ('#');
            if (hash > 0) {
                namespace = value.substring (1, hash + 1);
                prefix = prefixes.get (namespace);
                if (prefix == null) {
                    prefix = "p" + prefixes.keySet ().size ();
                    prefixes.put (namespace, prefix);
                }
                value = prefix + ":" + value.substring (hash + 1, value.length () - 1);
            }
        }

        return value;
    }

    private String instant (int index) {
        return uri (_namespace + "#instant" + index);
    }

    private String literal (Object value) {
        return "\"\"\"" + value + "\"\"\"";
    }

    private String node (GraphSerialisationIDs ids, Object node) {
        return uri (_namespace + "#" + ids.nodeID ((Node) node));
    }

    public final void setGraphNamespace (String graphsNamespace) {
        _namespace = graphsNamespace;
        if (_namespace.endsWith ("#")) {
            _namespace = _namespace.substring (0, _namespace.length () - 1);
        }
    }

    private String time (Date time) {
        return literal (dateToXSDDateTime (time));
    }

    private String uri (Object value) {
        return "<" + value + ">";
    }

    public void write (Graph graph, PrintWriter out, boolean pretty) throws IOException {
        Set<Triple> triples = translate (graph);
        Map<String, String> prefixes = extractNamespaces (triples);
        Set<String> subjects = getSubjects (triples);
        boolean firstPredicate, firstObject;

        for (String namespace : prefixes.keySet ()) {
            out.println ("@prefix " + prefixes.get (namespace) + ": <" + namespace + "> .");
        }
        for (String subject : subjects) {
            out.print (subject + " ");
            firstPredicate = true;
            for (String predicate : getPredicates (triples, subject)) {
                if (!firstPredicate) {
                    out.print ("; ");
                }
                out.print (predicate + " ");
                firstPredicate = false;
                firstObject = true;
                for (String object : getObjects (triples, subject, predicate)) {
                    if (!firstObject) {
                        out.print (", ");
                    }
                    out.print (object);
                    firstObject = false;
                }
            }
            out.println (".");
        }
    }

    private Set<Triple> translate (Graph graph) {
        GraphSerialisationIDs ids = new GraphSerialisationIDs (graph);
        Set<Triple> triples = new HashSet<Triple> ();
        String predicate = ":", timepredicate;
        int timeCount = 0;
        String cause, effect, instant;
        Time time;

        for (Account account : graph) {
            if (!account.isEmpty ()) {
                for (GraphElement element : account) {
                    if (element instanceof Agent) {
                        triples.add (new Triple (node (ids, element), "a", OPMV_AGENT));
                    }
                    if (element instanceof Artifact) {
                        triples.add (new Triple (node (ids, element), "a", OPMV_ARTIFACT));
                    }
                    if (element instanceof Process) {
                        triples.add (new Triple (node (ids, element), "a", OPMV_PROCESS));
                    }
                    if (element instanceof Edge) {
                        cause = node (ids, ((Edge) element).getCause ());
                        effect = node (ids, ((Edge) element).getEffect ());
                        timepredicate = null;
                        if (element instanceof RoleAnnotatedEdge && ((RoleAnnotatedEdge) element).getRole () != null) {
                            predicate = uri (((RoleAnnotatedEdge) element).getRole ());
                        } else {
                            if (element instanceof Used) {
                                predicate = OPMV_USED;
                                timepredicate = OPMV_WASUSEDAT;
                            }
                            if (element instanceof WasControlledBy) {
                                predicate = OPMV_WASCONTROLLEDBY;
                            }
                            if (element instanceof WasDerivedFrom) {
                                predicate = OPMV_WASDERIVEDFROM;
                            }
                            if (element instanceof WasGeneratedBy) {
                                predicate = OPMV_WASGENERATEDBY;
                                timepredicate = OPMV_WASGENERATEDAT;
                            }
                            if (element instanceof WasTriggeredBy) {
                                predicate = OPMV_WASTRIGGEREDBY;
                            }
                            if (element instanceof TimeAnnotatedEdge && timepredicate != null) {
                                time = ((TimeAnnotatedEdge) element).getTime ();
                                timeCount += 1;
                                instant = instant (timeCount);
                                triples.add (new Triple (effect, timepredicate, instant));
                                triples.add (new Triple (instant, "a", TIME_INSTANT));
                                triples.add (new Triple (instant, TIME_INXSD, time (time.getNoLaterThan ())));
                            }
                        }
                        triples.add (new Triple (effect, predicate, cause));
                    }
                }
            }
        }

        return triples;
    }

    private Set<String> getSubjects (Set<Triple> triples) {
        Set<String> subjects = new HashSet<String> ();

        for (Triple triple : triples) {
            subjects.add (triple._subject);
        }

        return subjects;
    }

    private Set<String> getPredicates (Set<Triple> triples, String subject) {
        Set<String> predicates = new HashSet<String> ();

        for (Triple triple : triples) {
            if (triple._subject.equals (subject)) {
                predicates.add (triple._subject);
            }
        }

        return predicates;
    }

    private Set<String> getObjects (Set<Triple> triples, String subject, String predicate) {
        Set<String> objects = new HashSet<String> ();

        for (Triple triple : triples) {
            if (triple._subject.equals (subject) && triple._predicate.equals (predicate)) {
                objects.add (triple._subject);
            }
        }

        return objects;
    }
}
