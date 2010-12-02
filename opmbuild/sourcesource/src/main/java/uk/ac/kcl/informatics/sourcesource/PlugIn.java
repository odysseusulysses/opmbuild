package uk.ac.kcl.informatics.sourcesource;

import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.Process;

public interface PlugIn {
    void process (Graph graph, Process occurred);
    void variable (Graph graph, Artifact occurred);
    void wasGeneratedBy (Graph graph, Artifact effect, String role, Process cause);
    void used (Graph graph, Process effect, Artifact cause, String role);
}
