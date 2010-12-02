package uk.ac.kcl.informatics.sourcesource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TXLApply {

    private File _adapted;
    private File _txlCommand;

    public TXLApply (File adapted, File txlCommand) {
        _adapted = adapted;
        _txlCommand = txlCommand;
    }

    public TXLApply (String adapted, String txlCommand) {
        this (new File (adapted), new File (txlCommand));
    }

    public TXLApply () {
        this ("adapted", "lib/txl/bin/txl.exe");
    }

    public static void main (String[] arguments) throws TXLException {
        String adapted = arguments[0];
        String txlCommand = arguments[1];
        String sources = arguments[2];
        String inPackage = arguments[3];
        String txl = arguments[4];

        new TXLApply (adapted, txlCommand).runTXLOnAll (sources, inPackage, txl);
    }

    public void runTXLOnAll (String sources, String inPackage, String txl) throws TXLException {
        runTXLOnAll (new File (sources), inPackage, new File (txl));
    }

    public void runTXLOnAll (File sources, String inPackage, File txl) throws TXLException {
        if (!_adapted.exists ()) {
            _adapted.mkdir ();
        }
        runTXLOnAll (sources, _adapted, inPackage, txl);
    }

    private void runTXLOnAll (File sourceLocal, File adaptLocal, String inPackage, File txl) throws TXLException {
        File nextAdapt;
        String name, nextPackage;

        for (File nextSource : sourceLocal.listFiles ()) {
            name = nextSource.getName ();
            nextAdapt = new File (adaptLocal, name);
            if (nextSource.isDirectory ()
                    && (inPackage.startsWith (name + ".") || inPackage.equals (name))) {
                if (!nextAdapt.exists ()) {
                    nextAdapt.mkdir ();
                }
                if (inPackage.equals (name)) {
                    nextPackage = "";
                } else {
                    nextPackage = inPackage.substring (name.length () + 1);
                }
                runTXLOnAll (nextSource, nextAdapt, nextPackage, txl);
            } else {
                if (nextSource.isFile () && nextSource.getName ().endsWith (".java")
                        && inPackage.trim ().equals ("")) {
                    runTXL (nextSource, nextAdapt, txl);
                }
            }
        }
    }

    public void runTXL (File source, File adapt, File txl) throws TXLException {
        try {
            String in = "\"" + source.getAbsolutePath () + "\"";
            String out = "\"" + adapt.getAbsolutePath () + "\"";
            String command = "\"" + _txlCommand.getAbsolutePath () + "\"";
            String trans = "\"" + txl + "\"";
            System.out.println (command + " -o " + out + " " + in + " " + trans);
            ProcessBuilder builder = new ProcessBuilder (command, "-o", out, in, trans);
            Process process = builder.start ();
            new StreamGobbler (process.getErrorStream ()).start ();
            System.out.println (process.waitFor ());
        } catch (InterruptedException ex) {
            throw new TXLException (ex);
        } catch (IOException ex) {
            throw new TXLException (ex);
        }
    }
}

class StreamGobbler extends Thread {

    InputStream _is;

    StreamGobbler (InputStream is) {
        this._is = is;
    }

    @Override
    public void run () {
        try {
            InputStreamReader isr = new InputStreamReader (_is);
            BufferedReader br = new BufferedReader (isr);
            String line = null;
            while ((line = br.readLine ()) != null) {
                System.out.println (line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace ();
        }
    }
}
