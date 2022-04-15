package com.makolab.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.makolab.utils.ByteArrayUtils.ToHexString;

public class Sha256HashCalculator implements HashCalculator {
    private final String SHA256_NAME = "SHA-256";
    private final int HASH_SIZE = 256;

    @Override
    public String calculateHash(String input) {
        byte[] digest = calculateHashAsBytes(input);
        return ToHexString(digest);
    }

    @Override
    public byte[] calculateHashAsBytes(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(SHA256_NAME);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String combineOrdered(String[] tup) {
        return null;
    }

    @Override
    public String combineUnordered(String[] tup) {
        return null;
    }

    @Override
    public int getHashSize() {
        return HASH_SIZE;
    }
}
