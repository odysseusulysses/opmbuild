package uk.ac.kcl.informatics.opmbuild;

public class Annotation extends AnnotationHandler implements GraphElement {
    private String _key;
    private Object _value;

    public Annotation () {
        this (null, null);
    }

    public Annotation (String key, Object value) {
        _key = key;
        _value = value;
    }

    /**
     * Returns true if the type and value of the other annotation are the same
     * @param other Annotation to be compared
     * @return True if both annotations have same type and value
     */
    @Override
    public boolean equals (Object other) {
        return (other instanceof Annotation) &&
                ((Annotation) other)._key.equals (_key) &&
                ((Annotation) other)._value.equals (_value);
    }

    public String getKey () {
        return _key;
    }

    public Object getValue () {
        return _value;
    }

    @Override
    public int hashCode () {
        return _key.hashCode ();
    }

    public void setKey (String key) {
        _key = key;
    }

    public void setValue (Object value) {
        _value = value;
    }
}
