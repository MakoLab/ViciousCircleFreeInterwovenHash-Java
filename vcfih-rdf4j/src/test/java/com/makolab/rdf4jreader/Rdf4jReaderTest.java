package com.makolab.rdf4jreader;

import com.makolab.utils.ByteArrayUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Rdf4jReaderTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "6SHGI4ZSSLCXXQSBB395.ttl",
            "8I5DZWZKVSZI1NUHU748.ttl",
            "549300NCY2P2FLJT9D42.ttl",
            "549300U5FI25Y6MFOS85.ttl",
            "2594007XIACKNMUAW223.ttl",
            "5493000C01ZX7D35SD85.ttl",
            "5493001KQW6DM7KEDR62.ttl",
            "0292001376F6T9DHG583.ttl",
            "F5WCUMTUM4RKZ1MAIE39.ttl",
            "MLU0ZO3ML4LN2LL2TL39.ttl",
            "PBLD0EJDB5FWOLXP3B76.ttl"
    })
    void beforeAndAfterBGHashesShouldBeTheSame(String fileName) {
        var is1 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/BeforeBG/" + fileName);
        var is2 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/AfterBG/" + fileName);
        try {
            var g1 = Rio.parse(is1, RDFFormat.TURTLE);
            var g2 = Rio.parse(is2, RDFFormat.TURTLE);
            var ig1 = Rdf4jReader.readGraph(g1);
            var ig2 = Rdf4jReader.readGraph(g2);
            var h1 = ByteArrayUtils.toHexString(ig1.calculateHash());
            var h2 = ByteArrayUtils.toHexString(ig2.calculateHash());
            assertEquals(h1, h2);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}