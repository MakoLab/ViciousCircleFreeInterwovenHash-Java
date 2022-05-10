package com.makolab.graphelements;

import com.makolab.utils.ByteArrayUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LiteralNode extends StandardNode {
    static MessageDigest digest;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public LiteralNode(String value, String datatype, String lang) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (
                    NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        if (datatype != null && lang != null) {
            throw new IllegalArgumentException("Datatype and language cannot be present at the same time.");
        }
        if (datatype != null) {
            switch (datatype) {
                case "http://www.w3.org/2001/XMLSchema#dateTime":
                case "http://www.w3.org/2001/XMLSchema#dateTimeStamp":
                    var nDate = LocalDateTime.ofInstant(ZonedDateTime.parse(value).toInstant(), ZoneOffset.UTC);
                    value = nDate.format(FORMATTER);
                    break;
                case "http://www.w3.org/2001/XMLSchema#decimal":
                    var nDecimal = Double.parseDouble(value);
                    value = Double.toString(nDecimal);
                    break;
                case "http://www.w3.org/2001/XMLSchema#string":
                    datatype = "";
                default:
                    value = Normalizer.normalize(value.replaceAll("\\p{C}", ""), Normalizer.Form.NFC);
            }
            value = escapeN3(value) + datatype;
        }
        if (lang != null) {
            value = escapeN3(value) + "@" + lang;
        }
        identifier = ByteArrayUtils.toHexString(digest.digest(identifier.getBytes(StandardCharsets.UTF_8)));
    }

    private String escapeN3(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }
}
