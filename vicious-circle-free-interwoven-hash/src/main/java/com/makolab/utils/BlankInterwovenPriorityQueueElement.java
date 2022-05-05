package com.makolab.utils;

import com.makolab.graphelements.Node;
import org.javatuples.Pair;
import org.javatuples.Septet;
import org.javatuples.Sextet;

import java.net.URI;

public class BlankInterwovenPriorityQueueElement implements Comparable<BlankInterwovenPriorityQueueElement> {

    Node value;
    Septet<Integer, Integer, Integer, Integer, Integer, String, String> tuple;

    public BlankInterwovenPriorityQueueElement(Node value, Septet<Integer, Integer, Integer, Integer, Integer, String, String> tuple) {
        this.value = value;
        this.tuple = tuple;
    }

    public Node getValue() {
        return value;
    }

    @Override
    public int compareTo(BlankInterwovenPriorityQueueElement o) {
        return this.tuple.compareTo(o.tuple);
    }
}
