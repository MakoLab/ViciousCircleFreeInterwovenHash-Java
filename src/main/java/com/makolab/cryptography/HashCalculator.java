package com.makolab.cryptography;

public interface HashCalculator {
    String calculateHash(String input);
    byte[] calculateHashAsBytes(String input);

    String combineOrdered(String[] tup);

    String combineUnordered(String[] tup);

    int getHashSize();
}
