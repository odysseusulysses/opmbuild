package uk.ac.kcl.informatics.opmbuild.query;

import uk.ac.kcl.informatics.opmbuild.Artifact;
import uk.ac.kcl.informatics.opmbuild.query.steps.HasAnnotation;
import uk.ac.kcl.informatics.opmbuild.query.steps.HasRole;
import uk.ac.kcl.informatics.opmbuild.query.steps.Input;
import uk.ac.kcl.informatics.opmbuild.query.steps.ToCauseNodes;
import uk.ac.kcl.informatics.opmbuild.query.steps.ToEdgesToCauses;

public class Queries {
    public static Query getCauses (Query effectMatch, String role) {
        return getCauses (effectMatch, new HasRole (role));
    }

    public static Query getCauses (Query effectMatch, Query roleMatch) {
        Query toEdges = new ToEdgesToCauses ().setName ("traverse edge");
        Query toNodes = new ToCauseNodes ().setName ("traverse node");

        effectMatch.addChild (toEdges);
        toEdges.addChild (roleMatch);
        roleMatch.addChild (toNodes);

        return effectMatch.asOneStep ();
    }

    /**
     * Returns a query for the (full, unfiltered) provenance of a graph element identified by
     * an annotation (key, value). The query result is the set of graph elements in the
     * provenance, which can be converted into a Graph using resultsToGraph().
     * @param startKey Key of the annotation identifying the start item.
     * @param startValue Value of the annotation identifying the start item.
     * @return A Query for the provenance of the identified start item.
     */
    public static Query provenance (String startKey, Object startValue) {
        return provenance (new HasAnnotation (startKey, startValue).setName ("find start"));
    }

    public static Query provenance (Artifact start) {
        return provenance (new Input (start).setName ("find start"));
    }

    public static Query provenance (Query findStart) {
        Query store = new MemoryStore ().setName ("store");
        Query toEdges = new ToEdgesToCauses ().setName ("traverse edge");
        Query toNodes = new ToCauseNodes ().setName ("traverse node");

        findStart.addChild (store).addChild (toEdges);
        toEdges.addChild (store).addChild (toNodes);
        toNodes.addChild (store).addChild (toEdges);

        return findStart.asOneStep ();
    }
}
