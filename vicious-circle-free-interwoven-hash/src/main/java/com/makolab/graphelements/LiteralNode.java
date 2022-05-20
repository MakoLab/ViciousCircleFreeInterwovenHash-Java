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
            }
        }
        value = Normalizer.normalize(value.replaceAll("\\p{C}", ""), Normalizer.Form.NFC);
        value = escapeN3(value);
        if (lang != null && !lang.isBlank()) {
            value = value + lang;
        }
        else {
            if (datatype != null && !datatype.isBlank()) {
                value = value + datatype;
            }
        }
        identifier = ByteArrayUtils.toHexString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
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
