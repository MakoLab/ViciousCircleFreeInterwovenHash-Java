package com.makolab.app;

import com.makolab.rdf4jreader.Rdf4jReader;
import com.makolab.utils.ByteArrayUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) {
        if (args.length == 0) {
            try (InputStream is = Program.class.getResourceAsStream("/rdf/rdf1.ttl")) {
                var g = Rio.parse(is, RDFFormat.TURTLE);
                var ig = Rdf4jReader.readGraph(g);
                var hash = ig.calculateHash();
                System.out.println(ByteArrayUtils.toHexString(hash));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            var format = getRDFFormat(args[1]);
            var file = new File(args[0]);
            if (file.isDirectory()) {
                parseDirectory(file, format);
            }
            else {
                parseFile(file, format);
            }
        }
    }

    private static void parseDirectory(File file, RDFFormat format) {
        var files = file.listFiles(f -> !f.isDirectory());
        if (files == null || files.length == 0) {
            System.out.println("No files found in directory");
            return;
        }
        System.out.printf("Found %d files in a directory.%n", files.length);
        var graphs = new ArrayList<Model>(files.length);
        for (var f : files) {
            try (var is = new FileInputStream(f)) {
                graphs.add(Rio.parse(is, format));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        var startAll = Instant.now();
        for (var g : graphs) {
            var ig = Rdf4jReader.readGraph(g);
            g.clear();
            var hash = ig.calculateHash();
            System.out.printf("Calculated hash: %s%n", ByteArrayUtils.toHexString(hash));
        }
        var endAll = Instant.now();
        System.out.printf("All hashes calculated in: %s", Duration.between(startAll, endAll));
    }

    private static void parseFile(File file, RDFFormat format) {
        try (var is = new FileInputStream(file)) {
            System.out.printf("Parsing file %s%n", file);
            var startAll = Instant.now();
            var g = Rio.parse(is, format);
            var endParse = Instant.now();
            System.out.printf("Parsing done in: %s%n", Duration.between(startAll, endParse));
            System.out.printf("Found %d triples.%n", g.size());

            var startRead = Instant.now();
            var ig = Rdf4jReader.readGraph(g);
            var endRead = Instant.now();
            g.clear();
            System.out.printf("Reading done in: %s%n", Duration.between(startRead, endRead));

            var startHash = Instant.now();
            var hash = ig.calculateHash();
            var endHash = Instant.now();
            System.out.printf("Hashing done in: %s%n", Duration.between(startHash, endHash));
            System.out.printf("Calculated hash: %s%n", ByteArrayUtils.toHexString(hash));
            System.out.printf("All done in: %s%n", Duration.between(startAll, endHash));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RDFFormat getRDFFormat(String format) {
        switch (format.toLowerCase()) {
            case "ttl":
            case "turtle":
                return RDFFormat.TURTLE;
            case "xml":
            case "rdf/xml":
                return RDFFormat.RDFXML;
            case "json":
            case "json-ld":
                return RDFFormat.JSONLD;
            case "n-triples":
            case "nt":
                return RDFFormat.NTRIPLES;
            default:
                throw new IllegalArgumentException("Unknown format");
        }
    }
}
