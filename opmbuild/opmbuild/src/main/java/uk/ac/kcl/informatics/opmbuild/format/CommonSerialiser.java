package uk.ac.kcl.informatics.opmbuild.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import uk.ac.kcl.informatics.opmbuild.Graph;

public abstract class CommonSerialiser {

    private static SimpleDateFormat _dateToXSDFormat = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ssZ");

    public void write (Graph graph, File out, boolean pretty) throws IOException {
        PrintWriter writer = new PrintWriter (new BufferedWriter (new FileWriter (out)));
        write (graph, writer, pretty);
        writer.close ();
    }

    public abstract void write (Graph graph, PrintWriter out, boolean pretty) throws IOException;

    public static String dateToXSDDateTime (Date date) {
        String xsd = _dateToXSDFormat.format (date);
        StringBuilder text = new StringBuilder (xsd);

        text.insert (22, ':');
        
        return text.toString ();
    }
}
