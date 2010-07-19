package uk.ac.kcl.informatics.opmbuild;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.kcl.informatics.opmbuild.format.gviz.GVizFormatException;
import uk.ac.kcl.informatics.opmbuild.format.poem.PoemReadException;
import uk.ac.kcl.informatics.opmbuild.tools.PoemToGViz;

public class PoemToGVizTest extends TestCase {

    private static final String TEST_DIR = "src/test/ext";
    private static final String POEM_FILE = TEST_DIR + "/kringla.poem";
    private static final String GVIZ_FILE = TEST_DIR + "/kringla.gviz";
    private static final String PDF_FILE = TEST_DIR + "/kringla.pdf";
    private static final String PAGE_DIR = TEST_DIR + "/kringlaPage";

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PoemToGVizTest (String testName) {
        super (testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite () {
        return new TestSuite (PoemToGVizTest.class);
    }

    public void testConvertToGViz () throws IOException, PoemReadException, GVizFormatException {
        PoemToGViz.run (POEM_FILE, GVIZ_FILE);
    }

    public void testConvertToGraphics () throws IOException, PoemReadException, GVizFormatException {
        PoemToGViz.run (POEM_FILE, PDF_FILE);
    }

    public void testConvertToWebpage () throws IOException, PoemReadException, GVizFormatException {
        PoemToGViz.run (POEM_FILE, PAGE_DIR);
    }
}
