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
        try (var is1 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/BeforeBG/" + fileName);
             var is2 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/AfterBG/" + fileName)
        ) {
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

    @Test
    public void simpleGraphTest()
    {
        try (var is = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf1.ttl")) {
            var g = Rio.parse(is, RDFFormat.TURTLE);
            var hash = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g));
            assertNotNull(hash);
            assertEquals("495e88dfba09abdf20c3d3377af5d7338c1c9b5898d1932f4c5f9591359b774d", hash);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void simpleGraphTestWithBlankNode()
    {
        // Graphs differ only by a blank node label - should give the same hash
        try (var is1 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf2.ttl");
             var is2 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf2a.ttl");
             var is3 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf2b.ttl")
        ) {
            var g1 = Rio.parse(is1, RDFFormat.TURTLE);
            var g2 = Rio.parse(is2, RDFFormat.TURTLE);
            var g3 = Rio.parse(is3, RDFFormat.TURTLE);
            var hash1 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g1));
            var hash2 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g2));
            var hash3 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g3));
            assertNotNull(hash1);
            assertNotNull(hash2);
            assertNotNull(hash3);
            assertEquals("46596a5ee27ade4659bb40fb2d34c84ee88ee0b46830333472c9109371b0ea67", hash1);
            assertEquals("46596a5ee27ade4659bb40fb2d34c84ee88ee0b46830333472c9109371b0ea67", hash2);
            assertEquals("46596a5ee27ade4659bb40fb2d34c84ee88ee0b46830333472c9109371b0ea67", hash3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void leiGraphTest()
    {
        try (var is = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf3.ttl")) {
            var g = Rio.parse(is, RDFFormat.TURTLE);
            var hash = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g));
            assertNotNull(hash);
            assertEquals("105b977a67544242e503de5b6da15e8b890fe8681e2ca10805eeeedf7d73f86b", hash);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testHashesOfSameDatabaseInDifferentOrderShouldRemainSame()
    {
        try (var is1 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf1.nt");
             var is2 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf2.nt");
             var is3 = Rdf4jReaderTest.class.getResourceAsStream("/rdf/rdf3.nt")
        ) {
            var g1 = Rio.parse(is1, RDFFormat.NTRIPLES);
            var g2 = Rio.parse(is2, RDFFormat.NTRIPLES);
            var g3 = Rio.parse(is3, RDFFormat.NTRIPLES);
            var hash1 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g1));
            var hash2 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g2));
            var hash3 = ByteArrayUtils.toHexString(Rdf4jReader.calculateHash(g3));
            assertNotNull(hash1);
            assertNotNull(hash2);
            assertNotNull(hash3);
            assertEquals("781305ab37fd328f57c757473181a62903cf271af976353a678a48a330fc7d6a", hash1);
            assertEquals("781305ab37fd328f57c757473181a62903cf271af976353a678a48a330fc7d6a", hash2);
            assertEquals("781305ab37fd328f57c757473181a62903cf271af976353a678a48a330fc7d6a", hash3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}