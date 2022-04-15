package com.makolab.graphelements;

import com.makolab.cryptography.HashCalculator;
import com.makolab.cryptography.Sha256HashCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Graph {
    private HashCalculator defaultHashCalculator = new Sha256HashCalculator();
    List<Triple> Triples  = new ArrayList<>();
    HashMap<String, BlankNode> BlankNodes = new HashMap<String, BlankNode>();
    HashMap<String, StandardNode> StandardNodes = new HashMap<String, StandardNode>();
    HashMap<String, List<String>> WeaklyCC;
    HashMap<Integer, byte[]> ComponentHashValue = new HashMap<Integer, byte[]>();
    public byte[] HashValue;

    //region Getters and Setters
    public List<Triple> getTriples() {
        return Triples;
    }

    public void setTriples(List<Triple> triples) {
        Triples = triples;
    }

    public HashMap<String, BlankNode> getBlankNodes() {
        return BlankNodes;
    }

    public void setBlankNodes(HashMap<String, BlankNode> blankNodes) {
        BlankNodes = blankNodes;
    }

    public HashMap<String, StandardNode> getStandardNodes() {
        return StandardNodes;
    }

    public void setStandardNodes(HashMap<String, StandardNode> standardNodes) {
        StandardNodes = standardNodes;
    }

    public HashMap<String, List<String>> getWeaklyCC() {
        return WeaklyCC;
    }

    public void setWeaklyCC(HashMap<String, List<String>> weaklyCC) {
        WeaklyCC = weaklyCC;
    }

    public HashMap<Integer, byte[]> getComponentHashValue() {
        return ComponentHashValue;
    }

    public void setComponentHashValue(HashMap<Integer, byte[]> componentHashValue) {
        ComponentHashValue = componentHashValue;
    }

    public byte[] getHashValue() {
        return HashValue;
    }

    public void setHashValue(byte[] hashValue) {
        HashValue = hashValue;
    }
    //endregion


}
