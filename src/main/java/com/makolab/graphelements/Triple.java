package com.makolab.graphelements;

import java.net.URI;

public class Triple {
    public Node Subject;
    public URI Predicate;
    public Node Object;

    public Triple(Node s, URI p, Node o) {
        Subject = s;
        Predicate = p;
        Object = o;
    }

    public String prepareTriple()
    {
        var conversionValue = new StringBuilder();
        conversionValue.append('<');
        conversionValue.append(Subject.Translate(NodeRole.Subject));
        conversionValue.append('>');
        conversionValue.append('<');
        conversionValue.append(Predicate.toString());
        conversionValue.append('>');
        conversionValue.append('<');
        conversionValue.append(Object.Translate(NodeRole.Object));
        conversionValue.append('>');
        return conversionValue.toString();
    }
}
