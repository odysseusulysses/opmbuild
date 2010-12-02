package uk.ac.kcl.informatics.opmbuild.format.xml.v11;

class DefaultValueSerialiser implements ValueSerialiser {

    @Override
    public String serialise (Object value) {
        return value.toString ();
    }

}
