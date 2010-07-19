package uk.ac.kcl.informatics.opmbuild.tools;

import java.io.File;
import java.io.IOException;
import uk.ac.kcl.informatics.opmbuild.Graph;
import uk.ac.kcl.informatics.opmbuild.format.gviz.GVizFormatException;
import uk.ac.kcl.informatics.opmbuild.format.gviz.GVizWriter;
import uk.ac.kcl.informatics.opmbuild.format.gviz.ImageWriter;
import uk.ac.kcl.informatics.opmbuild.format.gviz.WebPageWriter;
import uk.ac.kcl.informatics.opmbuild.format.poem.PoemReadException;
import uk.ac.kcl.informatics.opmbuild.format.poem.PoemReader;

public class PoemToGViz {

    public static void main (String[] arguments) throws IOException, PoemReadException, GVizFormatException {
        if (arguments.length < 2) {
            System.out.println ("Run with command-line options: <poem-file-path> <output-file>");
            System.out.println ("If the output file has a valid graphviz output extension (.pdf, .png, ...) then that file will be created");
            System.out.println ("If the output file has extension .gviz then the output file will be GraphViz source");
            System.out.println ("If the output file has no extension it is treated as a directory and a webpage will be created in that directory");
            System.out.println ();
            if (arguments.length == 0) {
                System.out.println ("No arguments given");
            }
            if (arguments.length == 1) {
                System.out.println ("One argument given: " + arguments[0]);
            }
            System.exit (0);
        }

        run (arguments[0], arguments[1]);
        System.exit (0);
    }

    public static void run (String poemFile, String outputFile) throws IOException, PoemReadException, GVizFormatException {
        PoemReader in = new PoemReader ();
        Graph graph;
        File out;

        graph = in.read (new File (poemFile));
        out = new File (outputFile);

        if (out.getName ().endsWith (".gviz")) {
            GVizWriter img = new GVizWriter (out);
            img.write (graph);
            img.close ();
            return;
        }
        if (out.getName ().contains (".")) {
            ImageWriter img = new ImageWriter (out);
            img.write (graph);
            return;
        }
        WebPageWriter page = new WebPageWriter (out);
        page.write (graph, "Kringla Manuscript OPM Graph");
    }
}
