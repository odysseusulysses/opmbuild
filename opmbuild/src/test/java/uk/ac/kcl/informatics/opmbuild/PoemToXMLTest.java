package uk.ac.kcl.informatics.opmbuild;

import java.io.IOException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.kcl.informatics.opmbuild.format.poem.PoemReadException;
import uk.ac.kcl.informatics.opmbuild.tools.PoemToXML;

public class PoemToXMLTest extends TestCase {

    private static final String TEST_DIR = "src/test/ext";
    private static final String POEM_FILE = TEST_DIR + "/kringla.poem";
    private static final String XML_FILE = TEST_DIR + "/kringla.xml";

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PoemToXMLTest (String testName) {
        super (testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite () {
        return new TestSuite (PoemToXMLTest.class);
    }

    public void testConvertToXML () throws IOException, PoemReadException {
        PoemToXML.run (POEM_FILE, XML_FILE);
    }
}
