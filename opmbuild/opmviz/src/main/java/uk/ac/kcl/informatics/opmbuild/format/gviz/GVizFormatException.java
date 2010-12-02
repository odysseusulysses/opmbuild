package uk.ac.kcl.informatics.opmbuild.format.gviz;

public class GVizFormatException extends Exception {
    public GVizFormatException (Throwable cause) {
        super (cause);
    }

    public GVizFormatException (String message) {
        super (message);
    }
}
