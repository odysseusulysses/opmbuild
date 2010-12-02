package uk.ac.kcl.informatics.opmbuild.query;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParameterSet extends AbstractCollection<Object> {
    private Map <String, Collection<Object>> _arguments;
    private Map <String, Collection<Object>> _metadata;

    public ParameterSet () {
        _arguments = new HashMap <String, Collection<Object>> ();
        _metadata = new HashMap <String, Collection<Object>> ();
    }

    public Collection<Object> getArgument (String key) {
        return _arguments.get (key);
    }

    public Collection<Object> getMetadata (String key) {
        return _metadata.get (key);
    }

    @Override
    public Iterator<Object> iterator () {
        return new ParameterSetIterator (this, _arguments.keySet ());
    }

    public void setArgument (String key, Collection<Object> value) {
        _arguments.put (key, value);
    }

    public void setMetadata (String key, Collection<Object> value) {
        _metadata.put (key, value);
    }

    @Override
    public int size () {
        int size = 0;

        for (Collection<Object> values : _arguments.values ()) {
            size += values.size ();
        }

        return size;
    }
}
