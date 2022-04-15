package com.makolab.graphelements;
import org.javatuples.Pair;
import org.javatuples.Septet;
import org.javatuples.Sextet;

import javax.naming.OperationNotSupportedException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class Node {
    String Identifier;
    HashMap<String, List<URI>> IncomingIris = new HashMap<String, List<URI>>();
    HashMap<String, List<URI>> IncomingBlanks = new HashMap<String, List<URI>>();
    int InDegree;
    int OutDegree;
    HashMap<String, List<URI>> IriNeighbours = new HashMap<String, List<URI>>();
    HashMap<String, List<URI>> BlankNeighbours = new HashMap<String, List<URI>>();
    int BlankInDegree;
    int BlankOutDegree;
    int TempDegree;
    int StructureNumber;
    int StructureLevel;

    //region Getters and Setters
    public String getIdentifier() {
        return Identifier;
    }

    public void setIdentifier(String identifier) {
        Identifier = identifier;
    }

    public HashMap<String, List<URI>> getIncomingIris() {
        return IncomingIris;
    }

    public void setIncomingIris(HashMap<String, List<URI>> incomingIris) {
        IncomingIris = incomingIris;
    }

    public HashMap<String, List<URI>> getIncomingBlanks() {
        return IncomingBlanks;
    }

    public void setIncomingBlanks(HashMap<String, List<URI>> incomingBlanks) {
        IncomingBlanks = incomingBlanks;
    }

    public int getInDegree() {
        return InDegree;
    }

    public void setInDegree(int inDegree) {
        InDegree = inDegree;
    }

    public int getOutDegree() {
        return OutDegree;
    }

    public void setOutDegree(int outDegree) {
        OutDegree = outDegree;
    }

    public HashMap<String, List<URI>> getIriNeighbours() {
        return IriNeighbours;
    }

    public void setIriNeighbours(HashMap<String, List<URI>> iriNeighbours) {
        IriNeighbours = iriNeighbours;
    }

    public HashMap<String, List<URI>> getBlankNeighbours() {
        return BlankNeighbours;
    }

    public void setBlankNeighbours(HashMap<String, List<URI>> blankNeighbours) {
        BlankNeighbours = blankNeighbours;
    }

    public int getBlankInDegree() {
        return BlankInDegree;
    }

    public void setBlankInDegree(int blankInDegree) {
        BlankInDegree = blankInDegree;
    }

    public int getBlankOutDegree() {
        return BlankOutDegree;
    }

    public void setBlankOutDegree(int blankOutDegree) {
        BlankOutDegree = blankOutDegree;
    }

    public int getTempDegree() {
        return TempDegree;
    }

    public void setTempDegree(int tempDegree) {
        TempDegree = tempDegree;
    }

    public int getStructureNumber() {
        return StructureNumber;
    }

    public void setStructureNumber(int structureNumber) {
        StructureNumber = structureNumber;
    }

    public int getStructureLevel() {
        return StructureLevel;
    }

    public void setStructureLevel(int structureLevel) {
        StructureLevel = structureLevel;
    }
    //endregion

    Node() {
        Identifier = "";
    }

    public boolean isBlank() {
        return this instanceof BlankNode;
    }

    void addBlankNeighbour(Node object, URI predicate)
    {
        if (BlankNeighbours.containsKey(object.Identifier))
        {
            BlankNeighbours.get(object.Identifier).add(predicate);
        }
            else
        {
            BlankNeighbours.put(object.Identifier, new ArrayList<>(List.of(predicate)));
        }
        BlankOutDegree++;
        OutDegree++;
    }

    void addRealNeighbour(Node object, URI predicate)
    {
        if (IriNeighbours.containsKey(object.Identifier))
        {
            IriNeighbours.get(object.Identifier).add(predicate);
        }
            else
        {
            IriNeighbours.put(object.Identifier, new ArrayList<>(List.of(predicate)));
        }
        OutDegree++;
    }

    void addIncomingBlank(Node subject, URI predicate)
    {
        if (IncomingBlanks.containsKey(subject.Identifier))
        {
            IncomingBlanks.get(subject.Identifier).add(predicate);
        }
        else
        {
            IncomingBlanks.put(subject.Identifier, new ArrayList<>(List.of(predicate)));
        }
        BlankInDegree++;
        InDegree++;
    }

    void addIncomingReal(Node subject, URI predicate)
    {
        if (IncomingIris.containsKey(subject.Identifier))
        {
            IncomingIris.get(subject.Identifier).add(predicate);
        }
        else
        {
            IncomingIris.put(subject.Identifier, new ArrayList<>(List.of(predicate)));
        }
        InDegree++;
    }

    Sextet<String, Integer, Integer, Integer, Integer, String> generateRealPriorityTuple(String predicate)
    {
        if (predicate == null) {
            predicate = "";
        }
        if (this instanceof BlankNode)
        {
            throw new IllegalArgumentException("This is method cannot be executed on a blank node object");
        }
        return new Sextet<>(Identifier, BlankInDegree, InDegree, BlankOutDegree, OutDegree, predicate);
    }

    Sextet<Integer, Integer, Integer, Integer, Integer, String> generateBlankPriorityTuple(String predicate)
    {
        if (predicate == null) {
            predicate = "";
        }
        if (!(this instanceof BlankNode))
        {
            throw new IllegalArgumentException("This is method cannot be executed on a real node object");
        }
        return new Sextet<>(StructureLevel, BlankInDegree, InDegree, BlankOutDegree, OutDegree, predicate);
    }

    Septet<Integer, Integer, Integer, Integer, Integer, String, String> generateBlankInterwovenPriorityTuple(Graph g, String predicate) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    public abstract String Translate(NodeRole role);
}
