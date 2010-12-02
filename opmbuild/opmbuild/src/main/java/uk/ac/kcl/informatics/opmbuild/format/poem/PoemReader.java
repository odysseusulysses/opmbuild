package uk.ac.kcl.informatics.opmbuild.format.poem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.Tree;
import uk.ac.kcl.informatics.opmbuild.Agent;
import uk.ac.kcl.informatics.opmbuild.Annotation;
import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.GraphElement;
import uk.ac.kcl.informatics.opmbuild.Node;
import uk.ac.kcl.informatics.opmbuild.OPMAnnotations;
import uk.ac.kcl.informatics.opmbuild.Process;
import uk.ac.kcl.informatics.opmbuild.Retrieve;
import uk.ac.kcl.informatics.opmbuild.Used;
import uk.ac.kcl.informatics.opmbuild.WasControlledBy;
import uk.ac.kcl.informatics.opmbuild.WasGeneratedBy;

public class PoemReader {
    private enum NodeType {
        agent, annotation, artifact, inOut, inputs, label, name, outputs, poem, process, reference
    };

    private Agent createAgent (Tree tree, Map<String, Node> references) throws PoemReadException {
        Agent agent = new Agent ();
        int index = 0;
        String label;
        Annotation note;

        if (isType (tree, index, NodeType.label)) {
            label = createLabel (tree.getChild (index));
            agent.annotate (OPMAnnotations.LABEL, label);
            index += 1;
        }
        if (isType (tree, index, NodeType.name)) {
            agent = (Agent) createName (tree.getChild (index), references);
            index += 1;
        }
        if (isType (tree, index, NodeType.reference)) {
            createReference (tree.getChild (index), agent, references);
            index += 1;
        }
        for (int after = index; after < tree.getChildCount (); after += 1) {
            note = createAnnotation (tree.getChild (after));
            agent.annotate (note);
        }

        return agent;
    }

    private Annotation createAnnotation (Tree tree) {
        String key = tree.getChild (0).getText ();
        String value = tree.getChild (1).getText ();

        return new Annotation (key, value);
    }

    private Artifact createArtifact (Tree tree, Map<String, Node> references) throws PoemReadException {
        Artifact artifact = new Artifact ();
        int index = 0;
        String label, role;
        Annotation note;

        role = tree.getChild (0).getText ();
        index += 1;
        if (isType (tree, index, NodeType.label)) {
            label = createLabel (tree.getChild (index));
            artifact.annotate (OPMAnnotations.LABEL, label);
            index += 1;
        }
        if (isType (tree, index, NodeType.name)) {
            artifact = (Artifact) createName (tree.getChild (index), references);
            index += 1;
        }
        artifact.annotate (ConstructionAnnotations.ROLE_IN_EDGE, role);
        if (isType (tree, index, NodeType.reference)) {
            createReference (tree.getChild (index), artifact, references);
            index += 1;
        }
        for (int after = index; after < tree.getChildCount (); after += 1) {
            note = createAnnotation (tree.getChild (after));
            artifact.annotate (note);
        }

        return artifact;
    }

    private Set<GraphElement> createInOut (Tree tree, Map<String, Node> references) throws PoemReadException {
        int index = 0;
        Set<Node> ins = new HashSet<Node> ();
        Set<Artifact> outs = new HashSet<Artifact> ();
        Set<GraphElement> subgraph = new HashSet<GraphElement> ();
        Process process;
        String role;

        if (getType (tree.getChild (index)) == NodeType.inputs) {
            ins = createInputs (tree.getChild (index), references);
            index += 1;
        }
        process = createProcess (tree.getChild (index));
        subgraph.add (process);
        index += 1;
        if (tree.getChildCount () > index) {
            outs = createOutputs (tree.getChild (index), references);
        }

        for (Node in : ins) {
            if (in instanceof Agent) {
                subgraph.add (new WasControlledBy (process, (Agent) in));
            } else {
                role = Retrieve.getValueByKey (in, ConstructionAnnotations.ROLE_IN_EDGE).toString ();
                Retrieve.removeAnnotationsByKey (in, ConstructionAnnotations.ROLE_IN_EDGE);
                subgraph.add (new Used (process, role, (Artifact) in));
            }
        }
        subgraph.addAll (ins);
        for (Artifact out : outs) {
            role = Retrieve.getValueByKey (out, ConstructionAnnotations.ROLE_IN_EDGE).toString ();
            Retrieve.removeAnnotationsByKey (out, ConstructionAnnotations.ROLE_IN_EDGE);
            subgraph.add (new WasGeneratedBy (out, role, process));
        }
        subgraph.addAll (outs);

        return subgraph;
    }

    private Set<Node> createInputs (Tree tree, Map<String, Node> references) throws PoemReadException {
        Set<Node> nodes = new HashSet<Node> ();

        for (int index = 0; index < tree.getChildCount (); index += 1) {
            switch (getType (tree.getChild (index))) {
                case agent:
                    nodes.add (createAgent (tree.getChild (index), references));
                    break;
                case artifact:
                    nodes.add (createArtifact (tree.getChild (index), references));
                    break;
            }
        }

        return nodes;
    }

    private String createLabel (Tree tree) throws PoemReadException {
        String text = tree.getChild (0).getText ();

        if (text.startsWith ("\"")) {
            return text.substring (1, text.length () - 1);
        } else {
            return text;
        }
    }

    private Node createName (Tree tree, Map<String, Node> references) {
        String id = tree.getChild (0).getText ();

        return references.get (id);
    }

    private Set<Artifact> createOutputs (Tree tree, Map<String, Node> references) throws PoemReadException {
        Set<Artifact> nodes = new HashSet<Artifact> ();

        for (int index = 0; index < tree.getChildCount (); index += 1) {
            nodes.add (createArtifact (tree.getChild (index), references));
        }

        return nodes;
    }

    private Graph createPoem (Tree tree) throws PoemReadException {
        Graph graph = new Graph ();
        Map<String, Node> references = new HashMap<String, Node> ();

        for (int index = 0; index < tree.getChildCount (); index += 1) {
            graph.noAccount ().addAll (createInOut (tree.getChild (index), references));
        }

        return graph;
    }

    private Process createProcess (Tree tree) throws PoemReadException {
        Process process = new Process ();
        int afterLabel = 0;
        String label;
        Annotation note;

        if (isType (tree, afterLabel, NodeType.label)) {
            label = createLabel (tree.getChild (afterLabel));
            process.annotate (OPMAnnotations.LABEL, label);
            afterLabel += 1;
        }
        for (int index = afterLabel; index < tree.getChildCount (); index += 1) {
            note = createAnnotation (tree.getChild (index));
            process.annotate (note);
        }

        return process;
    }

    private void createReference (Tree tree, Node node, Map<String, Node> references) {
        String id = tree.getChild (0).getText ();

        references.put (id, node);
    }

    private NodeType getType (Tree tree) throws PoemReadException {
        String text = tree.getText ().trim ();
        NodeType[] types = new NodeType[] {NodeType.agent, NodeType.annotation, NodeType.artifact, NodeType.inOut, NodeType.inputs, NodeType.label, NodeType.outputs, NodeType.name, NodeType.poem, NodeType.process, NodeType.reference};
        int length = text.length ();
        String name;

        for (NodeType type : types) {
            name = type.toString ();
            if (name.length () <= length && name.equalsIgnoreCase (text.substring (0, name.length ()))) {
                return type;
            }
        }

        throw new PoemReadException ("Unknown tree node type: " + text.substring (0, 5));
    }

    private boolean isType (Tree tree, int childIndex, NodeType type) throws PoemReadException {
        return tree.getChildCount () > childIndex && getType (tree.getChild (childIndex)) == type;
    }

    public Graph read (File in) throws IOException, PoemReadException {
        return createPoem (readTree (in));
    }

    private Tree readTree (File in) throws IOException, PoemReadException {
        return readTree (new FileInputStream (in));
    }

    private Tree readTree (InputStream in) throws IOException, PoemReadException {
        try {
            ANTLRInputStream text = new ANTLRInputStream (in);
            PoemLexer lex = new PoemLexer (text);
            CommonTokenStream tokens = new CommonTokenStream (lex);
            PoemParser parser = new PoemParser (tokens);
            PoemParser.poem_return r = parser.poem ();
            
            return (Tree) r.tree;
        } catch (RecognitionException failedParse) {
            throw new PoemReadException (failedParse);
        }
    }
}
