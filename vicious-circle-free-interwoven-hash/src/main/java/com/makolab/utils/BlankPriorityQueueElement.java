package com.makolab.utils;

import com.makolab.graphelements.BlankNode;
import com.makolab.graphelements.Node;
import org.javatuples.Pair;
import org.javatuples.Sextet;

import java.net.URI;

public class BlankPriorityQueueElement implements Comparable<BlankPriorityQueueElement> {

    Pair<Node, URI> value;
    Sextet<Integer, Integer, Integer, Integer, Integer, String> tuple;

    public BlankPriorityQueueElement(Pair<Node, URI> value, Sextet<Integer, Integer, Integer, Integer, Integer, String> tuple) {
        this.value = value;
        this.tuple = tuple;
    }

    public Pair<Node, URI> getValue() {
        return value;
    }

    @Override
    public int compareTo(BlankPriorityQueueElement o) {
        return this.tuple.compareTo(o.tuple);
    }
}
