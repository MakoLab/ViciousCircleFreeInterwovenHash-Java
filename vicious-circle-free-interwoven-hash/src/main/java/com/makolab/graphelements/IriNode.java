package com.makolab.graphelements;

import java.net.URI;

public class IriNode extends StandardNode {
    public IriNode(String identifier)
    {
        this.identifier = identifier;
    }

    public IriNode(URI identifier)
    {
        this.identifier = identifier.toString();
    }
}
