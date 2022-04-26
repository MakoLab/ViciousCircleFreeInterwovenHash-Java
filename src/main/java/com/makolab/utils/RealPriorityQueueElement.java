package com.makolab.utils;

import com.makolab.graphelements.BlankNode;
import com.makolab.graphelements.Node;
import org.javatuples.Pair;
import org.javatuples.Sextet;

import java.net.URI;

public class RealPriorityQueueElement implements Comparable<RealPriorityQueueElement> {

    Pair<Node, URI> value;
    Sextet<String, Integer, Integer, Integer, Integer, String> tuple;

    public RealPriorityQueueElement(Pair<Node, URI> value, Sextet<String, Integer, Integer, Integer, Integer, String> tuple) {
        this.value = value;
        this.tuple = tuple;
    }

    public Pair<Node, URI> getValue() {
        return value;
    }

    @Override
    public int compareTo(RealPriorityQueueElement o) {
        return this.tuple.compareTo(o.tuple);
    }
}
