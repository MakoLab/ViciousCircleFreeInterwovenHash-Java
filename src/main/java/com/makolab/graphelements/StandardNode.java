package com.makolab.graphelements;

public class StandardNode extends Node {
    @Override
    public String translate(NodeRole role) {
        var sb = new StringBuilder();
        sb.append("grounded_node::role:");
        sb.append(role == NodeRole.Subject ? "S" : "O");
        sb.append("::name:");
        sb.append(Identifier);
        return sb.toString();
    }
}
