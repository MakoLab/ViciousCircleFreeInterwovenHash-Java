package com.makolab.app;

import com.makolab.rdf4jreader.Rdf4jReader;
import com.makolab.utils.ByteArrayUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;

public class Program {
    public static void main(String[] args) {
        try (InputStream is = Program.class.getResourceAsStream("/rdf/rdf1.ttl")) {
            var g = Rio.parse(is, RDFFormat.TURTLE);
            var ig = Rdf4jReader.readGraph(g);
            var hash = ig.calculateHash();
            System.out.println(ByteArrayUtils.toHexString(hash));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
