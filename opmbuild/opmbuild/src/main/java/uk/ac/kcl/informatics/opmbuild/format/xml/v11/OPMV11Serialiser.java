package uk.ac.kcl.informatics.opmbuild.format.xml.v11;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.openprovenance.model.Account;
import org.openprovenance.model.AccountRef;
import org.openprovenance.model.Agent;
import org.openprovenance.model.AgentRef;
import org.openprovenance.model.Annotation;
import org.openprovenance.model.Artifact;
import org.openprovenance.model.OPMFactory;
import org.openprovenance.model.OPMGraph;
import org.openprovenance.model.OPMSerialiser;
import org.openprovenance.model.Overlaps;
import org.openprovenance.model.Process;
import org.openprovenance.model.Role;
import org.openprovenance.model.Used;
import org.openprovenance.model.WasControlledBy;
import org.openprovenance.model.WasDerivedFrom;
import org.openprovenance.model.WasGeneratedBy;
import org.openprovenance.model.WasTriggeredBy;
import uk.ac.kcl.informatics.opmbuild.Annotated;
import uk.ac.kcl.informatics.opmbuild.Edge;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.GraphElement;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.Retrieve;
import uk.ac.kcl.informatics.opmbuild.RoleAnnotatedEdge;
import uk.ac.kcl.informatics.opmbuild.format.GraphSerialisationIDs;

public class OPMV11Serialiser {

    private OPMSerialiser _writer;
    private OPMFactory _factory;
    private ValueSerialiser _values;
    /** The value used for undefined roles */
    public static final String UNDEFINED = "http://openprovenance.org/role#undefined";

    public OPMV11Serialiser (ValueSerialiser values) throws IOException {
        try {
            _writer = new OPMSerialiser ();
            _factory = new OPMFactory ();
            _values = values;
        } catch (JAXBException ex) {
            throw new IOException (ex);
        }
    }

    public OPMV11Serialiser () throws IOException {
        this (new DefaultValueSerialiser ());
    }

    public void write (Graph graph, Writer out, boolean format) throws IOException {
        try {
            StringWriter buffered = new StringWriter ();

            _writer.serialiseOPMGraph (buffered, convert (graph), format);
            out.write (buffered.toString ());
        } catch (JAXBException ex) {
            throw new IOException (ex);
        }
    }

    public void write (Graph graph, File out, boolean format) throws IOException {
        BufferedWriter writer = new BufferedWriter (new FileWriter (out));
        write (graph, writer, format);
        writer.close ();
    }

    public OPMGraph convert (Graph graph) {
        GraphSerialisationIDs ids = new GraphSerialisationIDs (graph);
        Map<String, Account> sAccounts = new HashMap<String, Account> ();
        Collection<Overlaps> sOverlaps = new LinkedList<Overlaps> ();
        Map<String, Process> sProcesses = new HashMap<String, Process> ();
        Map<String, Artifact> sArtifacts = new HashMap<String, Artifact> ();
        Map<String, Agent> sAgents = new HashMap<String, Agent> ();
        Map<String, Object> sEdges = new HashMap<String, Object> ();
        Collection<Annotation> sAnnotations = new LinkedList<Annotation> ();
        Role role = null;
        Collection<Account> subset;
        String id;
        Object effect;
        Object cause;
        Object converted;
        String roleName;

        for (uk.ac.kcl.informatics.opmbuild.Account account : graph) {
            if (!graph.isNoAccount (account)) {
                id = ids.accountID (account);
                sAccounts.put (id, _factory.newAccount (id));
            }
        }
        for (Set<Account> overlap : findOverlaps (graph, ids, sAccounts)) {
            sOverlaps.add (_factory.newOverlaps (overlap));
        }
        for (GraphElement element : graph.getElements ()) {
            if (element instanceof Node) {
                converted = null;
                subset = convert (Retrieve.getAccountsContaining (graph, element), ids, sAccounts);
                id = ids.nodeID ((Node) element);
                if (element instanceof uk.ac.kcl.informatics.opmbuild.Process) {
                    converted = _factory.newProcess (id, subset);
                    sProcesses.put (id, (Process) converted);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.Artifact) {
                    converted = _factory.newArtifact (id, subset);
                    sArtifacts.put (id, (Artifact) converted);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.Agent) {
                    converted = _factory.newAgent (id, subset);
                    sAgents.put (id, (Agent) converted);
                }
                if (converted != null) {
                    sAnnotations.addAll (convertAnnotations (element, converted, ids, subset));
                }
            }
        }
        for (GraphElement element : graph.getElements ()) {
            if (element instanceof Edge) {
                converted = null;
                subset = convert (Retrieve.getAccountsContaining (graph, element), ids, sAccounts);
                id = ids.edgeID ((Edge) element);
                if (element instanceof RoleAnnotatedEdge) {
                    roleName = ((RoleAnnotatedEdge) element).getRole ();
                    if (roleName != null) {
                        role = _factory.newRole (roleName);
                    } else {
                        role = _factory.newRole (UNDEFINED);
                    }
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.Used) {
                    effect = sProcesses.get (ids.nodeID (((Edge) element).getEffect ()));
                    cause = sArtifacts.get (ids.nodeID (((Edge) element).getCause ()));
                    converted = _factory.newUsed (id, (Process) effect, role, (Artifact) cause, subset);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.WasGeneratedBy) {
                    effect = sArtifacts.get (ids.nodeID (((Edge) element).getEffect ()));
                    cause = sProcesses.get (ids.nodeID (((Edge) element).getCause ()));
                    converted = _factory.newWasGeneratedBy (id, (Artifact) effect, role, (Process) cause, subset);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.WasControlledBy) {
                    effect = sProcesses.get (ids.nodeID (((Edge) element).getEffect ()));
                    cause = sAgents.get (ids.nodeID (((Edge) element).getCause ()));
                    converted = _factory.newWasControlledBy ((Process) effect, role, (Agent) cause, subset);
                    ((WasControlledBy) converted).setId (id);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.WasDerivedFrom) {
                    effect = sArtifacts.get (ids.nodeID (((Edge) element).getEffect ()));
                    cause = sArtifacts.get (ids.nodeID (((Edge) element).getCause ()));
                    converted = _factory.newWasDerivedFrom ((Artifact) effect, (Artifact) cause, subset);
                    ((WasDerivedFrom) converted).setId (id);
                }
                if (element instanceof uk.ac.kcl.informatics.opmbuild.WasTriggeredBy) {
                    effect = sProcesses.get (ids.nodeID (((Edge) element).getEffect ()));
                    cause = sProcesses.get (ids.nodeID (((Edge) element).getCause ()));
                    converted = _factory.newWasTriggeredBy ((Process) effect, (Process) cause, subset);
                    ((WasTriggeredBy) converted).setId (id);
                }
                sEdges.put (id, converted);
                if (converted != null) {
                    sAnnotations.addAll (convertAnnotations (element, converted, ids, subset));
                }
            }
        }

        return _factory.newOPMGraph (sAccounts.values (), sOverlaps, sProcesses.values (),
                sArtifacts.values (), sAgents.values (), sEdges.values (), sAnnotations);
    }

    private Collection<Account> convert (Collection<uk.ac.kcl.informatics.opmbuild.Account> accounts, GraphSerialisationIDs ids, Map<String, Account> map) {
        Collection<Account> converted = new LinkedList<Account> ();
        Account found;

        for (uk.ac.kcl.informatics.opmbuild.Account account : accounts) {
            found = map.get (ids.accountID (account));
            if (found != null) {
                converted.add (found);
            }
        }

        return converted;
    }

    private Collection<Annotation> convertAnnotations (Annotated original, Object converted, GraphSerialisationIDs ids, Collection<Account> subset) {
        Collection<Annotation> notes = new LinkedList<Annotation> ();
        String key, value;

        for (uk.ac.kcl.informatics.opmbuild.Annotation note : original.getAnnotations ()) {
            key = note.getKey ();
            value = _values.serialise (note.getValue ());
            if (converted instanceof Agent) {
                AgentRef ref = _factory.newAgentRef ((Agent) converted);
                Collection<AccountRef> accrs = new LinkedList<AccountRef> ();
                for (Account account : subset) {
                    accrs.add (_factory.newAccountRef (account));
                }
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), ref, key, value, accrs));
            }
            if (converted instanceof Artifact) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (Artifact) converted, key, value, subset));
            }
            if (converted instanceof Process) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (Process) converted, key, value, subset));
            }
            if (converted instanceof Used) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (Used) converted, key, value, subset));
            }
            if (converted instanceof WasControlledBy) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (WasControlledBy) converted, key, value, subset));
            }
            if (converted instanceof WasDerivedFrom) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (WasDerivedFrom) converted, key, value, subset));
            }
            if (converted instanceof WasGeneratedBy) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (WasGeneratedBy) converted, key, value, subset));
            }
            if (converted instanceof WasTriggeredBy) {
                notes.add (_factory.newAnnotation (ids.annotationID (original, note), (WasTriggeredBy) converted, key, value, subset));
            }
        }

        return notes;
    }

    private Set<Set<Account>> findOverlaps (Graph graph, GraphSerialisationIDs ids, Map<String, Account> accounts) {
        Set<Set<Account>> overlaps = new HashSet<Set<Account>> ();
        Set<Account> one;

        for (uk.ac.kcl.informatics.opmbuild.Account account1 : graph) {
            if (!graph.isNoAccount (account1)) {
                for (uk.ac.kcl.informatics.opmbuild.Account account2 : graph) {
                    if (account1 != account2 && !graph.isNoAccount (account2) && !Collections.disjoint (account1, account2)) {
                        one = new HashSet<Account> ();
                        one.add (accounts.get (ids.accountID (account1)));
                        one.add (accounts.get (ids.accountID (account2)));
                        overlaps.add (one);
                    }
                }
            }
        }

        return overlaps;
    }
}
