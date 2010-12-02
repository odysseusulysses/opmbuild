package uk.ac.kcl.informatics.opmbuild.format.gviz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import uk.ac.kcl.informatics.opmbuild.Annotation;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.GraphElement;

public class WebPageWriter {

    private File _directory;

    public WebPageWriter (File directory) {
        _directory = directory;
    }

    private String localise (String url) {
        int hash = url.lastIndexOf ('#');
        int slash = url.lastIndexOf ('/');

        if (hash >= 0 || slash >= 0) {
            if (hash > slash) {
                return url.substring (hash + 1);
            } else {
                return url.substring (slash + 1);
            }
        }

        return url;
    }

    public void write (Graph graph, String title) throws GVizFormatException {
        try {
            File graphic = new File (_directory, "graph.png");
            File page = new File (_directory, "index.html");
            ImageWriter img = new ImageWriter (graphic);
            List<GraphElement> elements;
            PrintWriter out;
            _directory.mkdirs ();
            img.setNumberEntities (true);
            elements = img.write (graph);
            out = new PrintWriter (page);
            out.println ("<HTML>");
            out.println ("<HEAD><TITLE>" + title + "</TITLE></HEAD>");
            out.println ("<BODY>");
            out.println ("<H1>" + title + "</H1>");
            out.println ("<IMG SRC=\"graph.png\"/>");
            writeAnnotations (out, elements);
            out.println ("</BODY>");
            out.println ("</HTML>");

            out.close ();
        } catch (FileNotFoundException ex) {
            throw new GVizFormatException (ex);
        }
    }

    private void writeAnnotations (PrintWriter out, List<GraphElement> elements) {
        Set<Annotation> annotations;

        out.println (" <TABLE CELLBORDER=1>");
        for (int index = 0; index < elements.size (); index += 1) {
            annotations = elements.get (index).getAnnotations ();
            if (!annotations.isEmpty ()) {
                out.println ("  <TR>");
                out.println ("   <TD COLSPAN=2><B>" + index + "</B></TD>");
                out.println ("  </TR>");
                for (Annotation note : annotations) {
                    out.println ("  <TR>");
                    out.print ("   <TD>" + localise (note.getKey ()) + "</TD>");
                    out.println ("   <TD>" + note.getValue () + "</TD>");
                    out.println ("  </TR>");
                }
            }
        }
        out.println ("</TABLE>");
    }
}
