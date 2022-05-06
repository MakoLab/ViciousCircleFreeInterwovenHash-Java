package com.makolab.graphelements;

import com.makolab.utils.ByteArrayUtils;
import org.eclipse.rdf4j.model.IRI;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class LiteralNode extends StandardNode {
    static MessageDigest digest;

    public LiteralNode(String value, IRI datatype, Optional<String> lang)
    {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (
                    NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        if (datatype != null && lang.isPresent())
        {
            throw new IllegalArgumentException("Datatype and language cannot be present at the same time.");
        }
        identifier = value;
        if (datatype != null)
        {
            identifier += "^^" + datatype;
        }
        if (lang.isPresent())
        {
            identifier += "@" + lang.get();
        }
        // TODO: Implement literal normalization
        identifier = ByteArrayUtils.toHexString(digest.digest(identifier.getBytes(StandardCharsets.UTF_8)));
    }
}
