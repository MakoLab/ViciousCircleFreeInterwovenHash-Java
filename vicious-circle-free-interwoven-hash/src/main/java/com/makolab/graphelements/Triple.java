package com.makolab.graphelements;

import java.net.URI;

public class Triple {
    public Node subject;
    public URI predicate;
    public Node object;

    public Triple(Node s, URI p, Node o) {
        subject = s;
        predicate = p;
        object = o;
    }

    public String prepareTriple() {
        var conversionValue = new StringBuilder();
        conversionValue.append('<');
        conversionValue.append(subject.translate(NodeRole.Subject));
        conversionValue.append('>');
        conversionValue.append('<');
        conversionValue.append(predicate.toString());
        conversionValue.append('>');
        conversionValue.append('<');
        conversionValue.append(object.translate(NodeRole.Object));
        conversionValue.append('>');
        return conversionValue.toString();
    }
}
