package uk.ac.kcl.informatics.opmbuild.format.opmv.v10;

class Triple {

    String _subject, _predicate, _object;

    Triple (String subject, String predicate, String object) {
        _subject = subject;
        _predicate = predicate;
        _object = object;
    }

    @Override
    public boolean equals (Object other) {
        return (other instanceof Triple)
                && ((Triple) other)._subject.equals (_subject)
                && ((Triple) other)._predicate.equals (_predicate)
                && ((Triple) other)._object.equals (_object);
    }

    @Override
    public int hashCode () {
        return _subject.hashCode () + _predicate.hashCode () + _object.hashCode ();
    }
}
