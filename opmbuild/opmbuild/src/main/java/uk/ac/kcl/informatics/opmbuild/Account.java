package uk.ac.kcl.informatics.opmbuild;

import java.util.HashSet;

public class Account extends HashSet <GraphElement> {

    public Account () {
    }

    public Account (Account solution) {
        super (solution);
    }
}
