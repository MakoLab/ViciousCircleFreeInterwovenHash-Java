package com.makolab.graphelements;

import com.makolab.utils.BlankPriorityQueueElement;
import com.makolab.utils.RealPriorityQueueElement;
import org.javatuples.Pair;
import org.javatuples.Septet;
import org.javatuples.Sextet;

import java.net.URI;
import java.util.*;

public abstract class Node {
    String identifier;
    HashMap<String, List<URI>> incomingIris = new HashMap<>();
    HashMap<String, List<URI>> incomingBlanks = new HashMap<>();
    int inDegree;
    int outDegree;
    HashMap<String, List<URI>> iriNeighbours = new HashMap<>();
    HashMap<String, List<URI>> blankNeighbours = new HashMap<>();
    int blankInDegree;
    int blankOutDegree;
    int tempDegree;
    int structureNumber;
    int structureLevel;

    //region Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public HashMap<String, List<URI>> getIncomingIris() {
        return incomingIris;
    }

    public void setIncomingIris(HashMap<String, List<URI>> incomingIris) {
        this.incomingIris = incomingIris;
    }

    public HashMap<String, List<URI>> getIncomingBlanks() {
        return incomingBlanks;
    }

    public void setIncomingBlanks(HashMap<String, List<URI>> incomingBlanks) {
        this.incomingBlanks = incomingBlanks;
    }

    public int getInDegree() {
        return inDegree;
    }

    public void setInDegree(int inDegree) {
        this.inDegree = inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    public HashMap<String, List<URI>> getIriNeighbours() {
        return iriNeighbours;
    }

    public void setIriNeighbours(HashMap<String, List<URI>> iriNeighbours) {
        this.iriNeighbours = iriNeighbours;
    }

    public HashMap<String, List<URI>> getBlankNeighbours() {
        return blankNeighbours;
    }

    public void setBlankNeighbours(HashMap<String, List<URI>> blankNeighbours) {
        this.blankNeighbours = blankNeighbours;
    }

    public int getBlankInDegree() {
        return blankInDegree;
    }

    public void setBlankInDegree(int blankInDegree) {
        this.blankInDegree = blankInDegree;
    }

    public int getBlankOutDegree() {
        return blankOutDegree;
    }

    public void setBlankOutDegree(int blankOutDegree) {
        this.blankOutDegree = blankOutDegree;
    }

    public int getTempDegree() {
        return tempDegree;
    }

    public void setTempDegree(int tempDegree) {
        this.tempDegree = tempDegree;
    }

    public int getStructureNumber() {
        return structureNumber;
    }

    public void setStructureNumber(int structureNumber) {
        this.structureNumber = structureNumber;
    }

    public int getStructureLevel() {
        return structureLevel;
    }

    public void setStructureLevel(int structureLevel) {
        this.structureLevel = structureLevel;
    }
    //endregion

    Node() {
        identifier = "";
    }

    public boolean isBlank() {
        return this instanceof BlankNode;
    }

    void addBlankNeighbour(Node object, URI predicate) {
        if (blankNeighbours.containsKey(object.identifier)) {
            blankNeighbours.get(object.identifier).add(predicate);
        } else {
            blankNeighbours.put(object.identifier, new ArrayList<>(List.of(predicate)));
        }
        blankOutDegree++;
        outDegree++;
    }

    void addRealNeighbour(Node object, URI predicate) {
        if (iriNeighbours.containsKey(object.identifier)) {
            iriNeighbours.get(object.identifier).add(predicate);
        } else {
            iriNeighbours.put(object.identifier, new ArrayList<>(List.of(predicate)));
        }
        outDegree++;
    }

    void addIncomingBlank(Node subject, URI predicate) {
        if (incomingBlanks.containsKey(subject.identifier)) {
            incomingBlanks.get(subject.identifier).add(predicate);
        } else {
            incomingBlanks.put(subject.identifier, new ArrayList<>(List.of(predicate)));
        }
        blankInDegree++;
        inDegree++;
    }

    void addIncomingReal(Node subject, URI predicate) {
        if (incomingIris.containsKey(subject.identifier)) {
            incomingIris.get(subject.identifier).add(predicate);
        } else {
            incomingIris.put(subject.identifier, new ArrayList<>(List.of(predicate)));
        }
        inDegree++;
    }

    Sextet<String, Integer, Integer, Integer, Integer, String> generateRealPriorityTuple(String predicate) {
        if (predicate == null) {
            predicate = "";
        }
        if (this instanceof BlankNode) {
            throw new IllegalArgumentException("This is method cannot be executed on a blank node object");
        }
        return new Sextet<>(identifier, blankInDegree, inDegree, blankOutDegree, outDegree, predicate);
    }

    Sextet<Integer, Integer, Integer, Integer, Integer, String> generateBlankPriorityTuple(String predicate) {
        if (predicate == null) {
            predicate = "";
        }
        if (!(this instanceof BlankNode)) {
            throw new IllegalArgumentException("This is method cannot be executed on a real node object");
        }
        return new Sextet<>(structureLevel, blankInDegree, inDegree, blankOutDegree, outDegree, predicate);
    }

    Septet<Integer, Integer, Integer, Integer, Integer, String, String> generateBlankInterwovenPriorityTuple(Graph g, String predicate) {
        if (!(this instanceof BlankNode)) {
            throw new IllegalArgumentException("This is method cannot be executed on a real node object");
        }
        if (predicate == null) {
            predicate = "";
        }
        var neighbours = "";

        // Adding real incoming neighbours to the hash material in predefined order
        var miniqueue1 = new PriorityQueue<RealPriorityQueueElement>();
        for (var neighbour : getIncomingIris().keySet()) {
            for (var pred : getIncomingIris().get(neighbour)) {
                var neigh = g.getStandardNodes().get(neighbour);
                miniqueue1.add(new RealPriorityQueueElement(new Pair<>(neigh, pred), neigh.generateRealPriorityTuple(pred.toString())));
            }
        }
        while (miniqueue1.size() > 0) {
            var edge = miniqueue1.poll();
            neighbours += new Triple(edge.getValue().getValue0(), edge.getValue().getValue1(), this).prepareTriple();
        }

        // Adding blank incoming neighbours to the hash material in predefined order
        var miniqueue2 = new PriorityQueue<BlankPriorityQueueElement>();
        for (var neighbour : getIncomingBlanks().keySet()) {
            for (var pred : getIncomingBlanks().get(neighbour)) {
                var neigh = g.getBlankNodes().get(neighbour);
                miniqueue2.add(new BlankPriorityQueueElement(new Pair<>(neigh, pred), neigh.generateBlankPriorityTuple(pred.toString())));
            }
        }
        while (miniqueue2.size() > 0) {
            var edge = miniqueue2.poll();
            neighbours += new Triple(edge.getValue().getValue0(), edge.getValue().getValue1(), this).prepareTriple();
        }

        // Adding real neighbours to the hash material in predefined order
        var miniqueue3 = new PriorityQueue<RealPriorityQueueElement>();
        for (var neighbour : getIriNeighbours().keySet()) {
            for (var pred : getIriNeighbours().get(neighbour)) {
                var neigh = g.getStandardNodes().get(neighbour);
                miniqueue3.add(new RealPriorityQueueElement(new Pair<>(neigh, pred), neigh.generateRealPriorityTuple(pred.toString())));
            }
        }
        while (miniqueue3.size() > 0) {
            var edge = miniqueue3.poll();
            neighbours += new Triple(edge.getValue().getValue0(), edge.getValue().getValue1(), this).prepareTriple();
        }

        // Adding blank neighbours to the hash material in predefined order
        var miniqueue4 = new PriorityQueue<BlankPriorityQueueElement>();
        for (var neighbour : getBlankNeighbours().keySet()) {
            for (var pred : getBlankNeighbours().get(neighbour)) {
                var neigh = g.getBlankNodes().get(neighbour);
                miniqueue4.add(new BlankPriorityQueueElement(new Pair<>(neigh, pred), neigh.generateBlankPriorityTuple(pred.toString())));
            }
        }
        while (miniqueue4.size() > 0) {
            var edge = miniqueue4.poll();
            neighbours += new Triple(edge.getValue().getValue0(), edge.getValue().getValue1(), this).prepareTriple();
        }

        return new Septet<>(getStructureLevel(), getBlankInDegree(), getInDegree(), getBlankOutDegree(), getOutDegree(), neighbours, predicate);
    }

    public abstract String translate(NodeRole role);
}
