package com.makolab.graphelements;

import com.makolab.cryptography.HashCalculator;
import com.makolab.cryptography.Sha256HashCalculator;
import com.makolab.utils.BlankInterwovenPriorityQueueElement;
import com.makolab.utils.ByteArrayUtils;

import java.net.URI;
import java.util.*;

public class Graph {
    private HashCalculator defaultHashCalculator = new Sha256HashCalculator();
    List<Triple> triples = new ArrayList<>();
    HashMap<String, BlankNode> blankNodes = new HashMap<String, BlankNode>();
    HashMap<String, StandardNode> standardNodes = new HashMap<String, StandardNode>();
    HashMap<String, List<String>> weaklyCC;
    HashMap<Integer, byte[]> componentHashValue = new HashMap<Integer, byte[]>();
    public byte[] hashValue;

    //region Getters and Setters
    public List<Triple> getTriples() {
        return triples;
    }

    public void setTriples(List<Triple> triples) {
        this.triples = triples;
    }

    public HashMap<String, BlankNode> getBlankNodes() {
        return blankNodes;
    }

    public void setBlankNodes(HashMap<String, BlankNode> blankNodes) {
        this.blankNodes = blankNodes;
    }

    public HashMap<String, StandardNode> getStandardNodes() {
        return standardNodes;
    }

    public void setStandardNodes(HashMap<String, StandardNode> standardNodes) {
        this.standardNodes = standardNodes;
    }

    public HashMap<String, List<String>> getWeaklyCC() {
        return weaklyCC;
    }

    public void setWeaklyCC(HashMap<String, List<String>> weaklyCC) {
        this.weaklyCC = weaklyCC;
    }

    public HashMap<Integer, byte[]> getComponentHashValue() {
        return componentHashValue;
    }

    public void setComponentHashValue(HashMap<Integer, byte[]> componentHashValue) {
        this.componentHashValue = componentHashValue;
    }

    public byte[] getHashValue() {
        return hashValue;
    }

    public void setHashValue(byte[] hashValue) {
        this.hashValue = hashValue;
    }
    //endregion

    public byte[] calculateHash() {
        return calculateHash(defaultHashCalculator);
    }

    public byte[] calculateHash(HashCalculator hashCalculator) {
        var hashValueForGraph = new byte[hashCalculator.getHashSize() / 8];
        weaklyCC = treeMarking();
        for (var component : weaklyCC.keySet()) {
            var leadNode = weaklyCC.get(component).get(0);
            prepareSingleComponent(weaklyCC.get(component), true);
            var q = hashCalculator.calculateHashAsBytes(prepareSingleComponent(weaklyCC.get(component), false));
            componentHashValue.put(blankNodes.get(leadNode).structureNumber, q);
            hashValueForGraph = ByteArrayUtils.addHashes(hashValueForGraph, q);
        }

        for (var t : triples) {
            if (t.subject.isBlank() && t.object.isBlank())
                continue;
            else {
                var q = hashCalculator.calculateHashAsBytes(t.prepareTriple());
                hashValueForGraph = ByteArrayUtils.addHashes(hashValueForGraph, q);
            }
        }
        hashValue = hashValueForGraph;
        return hashValue;
    }

    public boolean containsBlankNode(BlankNode bn) {
        return blankNodes.containsKey(bn.identifier);
    }

    public boolean containsIriNode(StandardNode iNode) {
        return standardNodes.containsKey(iNode.identifier);
    }

    public void addTriple(Node s, URI p, Node o) {
        if (s instanceof BlankNode) {
            if (!blankNodes.containsKey(s.identifier)) {
                blankNodes.put(s.identifier, (BlankNode) s);
            }
            s = blankNodes.get(s.identifier);
        } else {
            if (!standardNodes.containsKey(s.identifier)) {
                standardNodes.put(s.identifier, (StandardNode) s);
            }
            s = standardNodes.get(s.identifier);
        }

        if (o instanceof BlankNode) {
            if (!blankNodes.containsKey(o.identifier)) {
                blankNodes.put(o.identifier, (BlankNode) o);
            }
            o = blankNodes.get(o.identifier);
        } else {
            if (!standardNodes.containsKey(o.identifier)) {
                standardNodes.put(o.identifier, (StandardNode) o);
            }
            o = standardNodes.get(o.identifier);
        }

        if (s instanceof BlankNode) {
            o.addIncomingBlank(s, p);
        } else {
            o.addIncomingReal(s, p);
        }

        if (o instanceof BlankNode) {
            s.addBlankNeighbour(o, p);
        } else {
            s.addRealNeighbour(o, p);
        }

        triples.add(new Triple(s, p, o));
    }

    public boolean cycleDetection() {
        var bg = copyBlanks();
        var queue = new LinkedList<BlankNode>();
        var visited = new ArrayList<BlankNode>();
        for (var neighbour : bg.values()) {
            neighbour.tempDegree = neighbour.blankInDegree;
        }
        for (var node : bg.values()) {
            if (node.blankInDegree == 0) {
                queue.add(node);
            }
        }
        while (queue.size() > 0) {
            var node = queue.poll();
            for (var neighbour : node.blankNeighbours.keySet()) {
                bg.get(neighbour).tempDegree -= node.blankNeighbours.get(neighbour).size();
                if (bg.get(neighbour).tempDegree == 0) {
                    queue.add(bg.get(neighbour));
                }
            }
            visited.add(node);
        }
        return visited.size() != bg.size();
    }

    public HashMap<String, List<String>> treeMarking() {
        HashMap<String, String> parent = new HashMap<>();
        for (var e : blankNodes.keySet()) {
            parent.put(e, e);
        }
        ArrayList<String[]> edges = new ArrayList<>();
        for (var e : blankNodes.values()) {
            for (var n : e.blankNeighbours.keySet()) {
                edges.add(new String[]{e.identifier, n});
            }
        }
        for (var e : edges) {
            parent.put(merge(parent, e[0]), merge(parent, e[1]));
        }

        var numberOfComponents = 0;
        for (var p : parent.keySet()) {
            numberOfComponents += p == parent.get(p) ? 1 : 0;
        }
        for (var p : parent.keySet()) {
            parent.put(p, merge(parent, parent.get(p)));
        }

        var result = new HashMap<String, List<String>>();
        for (var p : parent.keySet()) {
            if (result.containsKey(parent.get(p))) {
                result.get(parent.get(p)).add(p);
            } else {
                result.put(parent.get(p), new ArrayList<>(Arrays.asList(p)));
            }
        }

        var i = 0;
        for (var r : result.keySet()) {
            for (var n : result.get(r)) {
                blankNodes.get(n).structureNumber = i;
            }
            i++;
        }

        return result;
    }

    public String prepareSingleComponent(List<String> component, boolean preparing) {
        var valueForComponent = "";
        var priorityQueue = new PriorityQueue<BlankInterwovenPriorityQueueElement>();
        for (var node : component) {
            blankNodes.get(node).tempDegree = blankNodes.get(node).blankInDegree;
            if (blankNodes.get(node).blankInDegree == 0) {
                priorityQueue.add(new BlankInterwovenPriorityQueueElement(blankNodes.get(node), blankNodes.get(node).generateBlankInterwovenPriorityTuple(this, null)));
                blankNodes.get(node).structureLevel = 0;
            }
        }
        while (priorityQueue.size() > 0) {
            var node = priorityQueue.poll();

            // Get all edges coming out of node for hashing

            if (!preparing) {
                var miniqueue1 = new PriorityQueue<BlankInterwovenPriorityQueueElement>();
                for (var neighbour : node.getValue().blankNeighbours.keySet()) {
                    miniqueue1.add(new BlankInterwovenPriorityQueueElement(blankNodes.get(neighbour), blankNodes.get(neighbour).generateBlankInterwovenPriorityTuple(this, null)));
                }
                while (miniqueue1.size() > 0) {
                    var neigh = miniqueue1.poll();
                    var sorted = node.getValue().blankNeighbours.get(neigh.getValue().identifier);
                    Collections.sort(sorted);
                    for (var predicate : sorted) {
                        valueForComponent += new Triple(node.getValue(), predicate, neigh.getValue()).prepareTriple();
                    }
                }
            }

            // Proceed with handling subsequent parts of our DAG.

            for (var neighbour : node.getValue().blankNeighbours.keySet()) {
                blankNodes.get(neighbour).tempDegree -= node.getValue().blankNeighbours.get(neighbour).size();
                if (blankNodes.get(neighbour).tempDegree == 0) {
                    if (preparing) {
                        blankNodes.get(neighbour).structureLevel = node.getValue().structureLevel + 1;
                    }
                    priorityQueue.add(new BlankInterwovenPriorityQueueElement(blankNodes.get(neighbour), blankNodes.get(neighbour).generateBlankInterwovenPriorityTuple(this, null)));
                }
            }
        }
        return valueForComponent;
    }

    public HashMap<String, BlankNode> copyBlanks() {
        return new HashMap<>(blankNodes);
    }

    private static String merge(HashMap<String, String> parent, String x) {
        if (parent.get(x) == x) {
            return x;
        } else {
            return merge(parent, parent.get(x));
        }
    }
}
