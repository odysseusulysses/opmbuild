package uk.ac.kcl.informatics.opmbuild.format.poem;

public class PoemReadException extends Exception {
    public PoemReadException (String message) {
        super (message);
    }

    public PoemReadException (Throwable cause) {
        super (cause);
    }
}
