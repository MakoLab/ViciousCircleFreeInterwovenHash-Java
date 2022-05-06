package com.makolab.graphelements;

public class BlankNode extends Node {
    public BlankNode(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String translate(NodeRole role) {
        var sb = new StringBuilder();
        sb.append("blvl:");
        sb.append(structureLevel);
        sb.append("::bind:");
        sb.append(blankInDegree);
        sb.append("::ind:");
        sb.append(inDegree);
        sb.append("::boud:");
        sb.append(blankOutDegree);
        sb.append(":outd:");
        sb.append(outDegree);
        sb.append("::role:");
        sb.append(role == NodeRole.Subject ? "Sblank" : "Oblank");
        return sb.toString();
    }
}
