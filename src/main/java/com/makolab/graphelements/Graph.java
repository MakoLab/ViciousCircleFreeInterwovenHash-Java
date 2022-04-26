package com.makolab.graphelements;

import com.makolab.cryptography.HashCalculator;
import com.makolab.cryptography.Sha256HashCalculator;
import com.makolab.utils.BlankInterwovenPriorityQueueElement;
import com.makolab.utils.ByteArrayUtils;

import java.net.URI;
import java.util.*;

public class Graph {
    private HashCalculator defaultHashCalculator = new Sha256HashCalculator();
    List<Triple> Triples = new ArrayList<>();
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

    public byte[] calculateHash()
    {
        return calculateHash(defaultHashCalculator);
    }

    public byte[] calculateHash(HashCalculator hashCalculator)
    {
        var hashValueForGraph = new byte[hashCalculator.getHashSize() / 8];
        WeaklyCC = treeMarking();
        for (var component : WeaklyCC.keySet())
        {
            var leadNode = WeaklyCC.get(component).get(0);
            prepareSingleComponent(WeaklyCC.get(component), true);
            var q = hashCalculator.calculateHashAsBytes(prepareSingleComponent(WeaklyCC.get(component), false));
            ComponentHashValue.put(BlankNodes.get(leadNode).StructureNumber, q);
            hashValueForGraph = ByteArrayUtils.addHashes(hashValueForGraph, q);
        }

        for (var t : Triples)
        {
            if (t.Subject.isBlank() && t.Object.isBlank())
                continue;
            else
            {
                var q = hashCalculator.calculateHashAsBytes(t.prepareTriple());
                hashValueForGraph = ByteArrayUtils.addHashes(hashValueForGraph, q);
            }
        }
        HashValue = hashValueForGraph;
        return HashValue;
    }

    public boolean containsBlankNode(BlankNode bn) {
        return BlankNodes.containsKey(bn.Identifier);
    }
    public boolean containsIriNode(StandardNode iNode) {
        return StandardNodes.containsKey(iNode.Identifier);
    }

    public void addTriple(Node s, URI p, Node o)
    {
        if (s instanceof BlankNode)
        {
            if (!BlankNodes.containsKey(s.Identifier))
            {
                BlankNodes.put(s.Identifier, (BlankNode)s);
            }
            s = BlankNodes.get(s.Identifier);
        }
            else
        {
            if (!StandardNodes.containsKey(s.Identifier))
            {
                StandardNodes.put(s.Identifier, (StandardNode)s);
            }
            s = StandardNodes.get(s.Identifier);
        }

        if (o instanceof BlankNode)
        {
            if (!BlankNodes.containsKey(o.Identifier))
            {
                BlankNodes.put(o.Identifier, (BlankNode)o);
            }
            o = BlankNodes.get(o.Identifier);
        }
            else
        {
            if (!StandardNodes.containsKey(o.Identifier))
            {
                StandardNodes.put(o.Identifier, (StandardNode)o);
            }
            o = StandardNodes.get(o.Identifier);
        }

        if (s instanceof BlankNode)
        {
            o.addIncomingBlank(s, p);
        }
            else
        {
            o.addIncomingReal(s, p);
        }

        if (o instanceof BlankNode)
        {
            s.addBlankNeighbour(o, p);
        }
            else
        {
            s.addRealNeighbour(o, p);
        }

        Triples.add(new Triple(s, p, o));
    }

    public boolean cycleDetection()
    {
        var bg = copyBlanks();
        var queue = new LinkedList<BlankNode>();
        var visited = new ArrayList<BlankNode>();
        for (var neighbour : bg.values())
        {
            neighbour.TempDegree = neighbour.BlankInDegree;
        }
        for (var node : bg.values())
        {
            if (node.BlankInDegree == 0)
            {
                queue.add(node);
            }
        }
        while (queue.size() > 0)
        {
            var node = queue.poll();
            for (var neighbour : node.BlankNeighbours.keySet())
            {
                bg.get(neighbour).TempDegree -= node.BlankNeighbours.get(neighbour).size();
                if (bg.get(neighbour).TempDegree == 0)
                {
                    queue.add(bg.get(neighbour));
                }
            }
            visited.add(node);
        }
        return visited.size() != bg.size();
    }

    public HashMap<String, List<String>> treeMarking()
    {
        HashMap<String, String> parent = new HashMap<>();
        for (var e : BlankNodes.keySet())
        {
            parent.put(e, e);
        }
        ArrayList<String[]> edges = new ArrayList<>();
        for (var e : BlankNodes.values())
        {
            for (var n : e.BlankNeighbours.keySet())
            {
                edges.add(new String[] { e.Identifier, n });
            }
        }
        for (var e : edges)
        {
            parent.put(merge(parent, e[0]), merge(parent, e[1]));
        }

        var numberOfComponents = 0;
        for (var p : parent.keySet())
        {
            numberOfComponents += p == parent.get(p) ? 1 : 0;
        }
        for (var p : parent.keySet())
        {
            parent.put(p, merge(parent, parent.get(p)));
        }

        var result = new HashMap<String, List<String>>();
        for (var p : parent.keySet())
        {
            if (result.containsKey(parent.get(p)))
            {
                result.get(parent.get(p)).add(p);
            }
            else
            {
                result.put(parent.get(p), Arrays.asList(p));
            }
        }

        var i = 0;
        for (var r : result.keySet())
        {
            for (var n : result.get(r))
            {
                BlankNodes.get(n).StructureNumber = i;
            }
            i++;
        }

        return result;
    }

    public String prepareSingleComponent(List<String> component, boolean preparing)
    {
        var valueForComponent = "";
        var priorityQueue = new PriorityQueue<BlankInterwovenPriorityQueueElement>();
        for (var node : component)
        {
            BlankNodes.get(node).TempDegree = BlankNodes.get(node).BlankInDegree;
            if (BlankNodes.get(node).BlankInDegree == 0)
            {
                priorityQueue.add(new BlankInterwovenPriorityQueueElement(BlankNodes.get(node), BlankNodes.get(node).generateBlankInterwovenPriorityTuple(this, null)));
                BlankNodes.get(node).StructureLevel = 0;
            }
        }
        while (priorityQueue.size() > 0)
        {
            var node = priorityQueue.poll();

            // Get all edges coming out of node for hashing

            if (!preparing)
            {
                var miniqueue1 = new PriorityQueue<BlankInterwovenPriorityQueueElement>();
                for (var neighbour : node.getValue().BlankNeighbours.keySet())
                {
                    miniqueue1.add(new BlankInterwovenPriorityQueueElement(BlankNodes.get(neighbour), BlankNodes.get(neighbour).generateBlankInterwovenPriorityTuple(this, null)));
                }
                while (miniqueue1.size() > 0)
                {
                    var neigh = miniqueue1.poll();
                    var sorted = node.getValue().BlankNeighbours.get(neigh.getValue().Identifier); //ToDo: add sorting
                    for (var predicate : sorted)
                    {
                        valueForComponent += new Triple(node.getValue(), predicate, neigh.getValue()).prepareTriple();
                    }
                }
            }

            // Proceed with handling subsequent parts of our DAG.

            for (var neighbour : node.getValue().BlankNeighbours.keySet())
            {
                BlankNodes.get(neighbour).TempDegree -= node.getValue().BlankNeighbours.get(neighbour).size();
                if (BlankNodes.get(neighbour).TempDegree == 0)
                {
                    if (preparing)
                    {
                        BlankNodes.get(neighbour).StructureLevel = node.getValue().StructureLevel + 1;
                    }
                    priorityQueue.add(new BlankInterwovenPriorityQueueElement(BlankNodes.get(neighbour), BlankNodes.get(neighbour).generateBlankInterwovenPriorityTuple(this, null)));
                }
            }
        }
        return valueForComponent;
    }

    public HashMap<String, BlankNode> copyBlanks()
    {
        return new HashMap<>(BlankNodes);
    }

    private static String merge(HashMap<String, String> parent, String x)
    {
        if (parent.get(x) == x)
        {
            return x;
        }
        else
        {
            return merge(parent, parent.get(x));
        }
    }
}
