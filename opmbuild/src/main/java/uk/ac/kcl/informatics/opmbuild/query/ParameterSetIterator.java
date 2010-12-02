package uk.ac.kcl.informatics.opmbuild.query;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

class ParameterSetIterator implements Iterator<Object> {
    private ParameterSet _set;
    private Iterator<String> _keys;
    private Iterator<Object> _current;

    ParameterSetIterator (ParameterSet set, Set<String> keys) {
        _set = set;
        _keys = keys.iterator ();
        moveToNextCollection ();
    }

    public boolean hasNext () {
        while (_current != null && !_current.hasNext ()) {
            moveToNextCollection ();
        }
        if (_current == null) {
            return false;
        }
        return true;
    }

    private void moveToNextCollection () {
        if (_keys.hasNext ()) {
            _current = _set.getArgument (_keys.next ()).iterator ();
        } else {
            _current = null;
        }
    }

    public Object next () {
        if (!hasNext ()) {
            throw new NoSuchElementException ();
        }
        return _current.next ();
    }

    public void remove () {
        throw new UnsupportedOperationException ("Remove not supported on ParameterSet iterator.");
    }
}
