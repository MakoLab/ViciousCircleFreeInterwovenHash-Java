package com.makolab.graphelements;

import com.makolab.utils.ByteArrayUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void sameDateTimeInDifferentZoneShouldGiveSameHash() throws URISyntaxException {
        var g1 = new Graph();
        g1.addTriple(new IriNode("uri:id1"), new URI("p1:pred"), new LiteralNode("2002-05-30T09:30:10+06:00", "http://www.w3.org/2001/XMLSchema#dateTime", null));
        var h1 = ByteArrayUtils.toHexString(g1.calculateHash());
        var g2 = new Graph();
        g2.addTriple(new IriNode("uri:id1"), new URI("p1:pred"), new LiteralNode("2002-05-30T03:30:10Z", "http://www.w3.org/2001/XMLSchema#dateTime", null));
        var h2 = ByteArrayUtils.toHexString(g2.calculateHash());
        assertEquals(h1, h2);
    }
}