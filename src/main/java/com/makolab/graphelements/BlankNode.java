package com.makolab.graphelements;

public class BlankNode extends Node {
    public BlankNode(String identifier) {
        Identifier = identifier;
    }

    @Override
    public String Translate(NodeRole role) {
        var sb = new StringBuilder();
        sb.append("blvl:");
        sb.append(StructureLevel);
        sb.append("::bind:");
        sb.append(BlankInDegree);
        sb.append("::ind:");
        sb.append(InDegree);
        sb.append("::boud:");
        sb.append(BlankOutDegree);
        sb.append(":outd:");
        sb.append(OutDegree);
        sb.append("::role:");
        sb.append(role == NodeRole.Subject ? "Sblank" : "Oblank");
        return sb.toString();
    }
}
