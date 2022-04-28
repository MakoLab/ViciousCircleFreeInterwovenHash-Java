package com.makolab.utils;

import java.util.Collection;

public class ByteArrayUtils {
    public static String toHexString(byte[] bytes) {
        char[] c = new char[bytes.length * 2];
        int b;
        for (int i = 0; i < bytes.length; i++) {
            b = ((bytes[i] >> 4) & 0xF);
            c[i * 2] = (char) (87 + b + (((b - 10) >> 31) & -39));
            b = (bytes[i] & 0xF);
            c[i * 2 + 1] = (char) (87 + b + (((b - 10) >> 31) & -39));
        }
        return new String(c);
    }

    public static byte[] toByteArray(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException("Hex string cannot have odd number of digits.");
        }
        var byteArrLength = hexString.length() >> 1;
        var result = new byte[byteArrLength];
        int val, upper, res;
        for (var i = 0; i < byteArrLength; i++) {
            val = hexString.charAt(i << 1);
            upper = val + (((96 - val) >> 31) & -32);
            res = (((64 - upper) >> 31) & -7) + upper - 48;
            if (res < 0 || res > 15) {
                throw new NumberFormatException(String.format("Char %c is not a proper hex char.", hexString.charAt(i << 1)));
            }
            result[i] = (byte) (res << 4);
            val = hexString.charAt((i << 1) + 1);
            upper = val + (((96 - val) >> 31) & -32);
            res = (((64 - upper) >> 31) & -7) + upper - 48;
            if (res < 0 || res > 15) {
                throw new NumberFormatException(String.format("Char %c is not a proper hex char.", hexString.charAt((i << 1) + 1)));
            }
            result[i] |= (byte) res;
        }
        return result;
    }

    public static byte[] addHashes(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Array lengths must be the same.");
        }
        int sum = 0;
        var result = new byte[a.length];
        for (var i = a.length - 1; i >= 0; i--) {
            sum += (a[i] & 0xFF) + (b[i] & 0xFF);
            result[i] = (byte) (sum & 0x000000FF);
            sum >>= 8;
        }
        return result;
    }

    public static byte[] addHashes(Collection<byte[]> hashes) {
        if (hashes.isEmpty()) {
            throw new IllegalArgumentException("Hash collection is empty");
        }
        byte[] sum = new byte[hashes.iterator().next().length];
        for (var hash : hashes) {
            sum = addHashes(sum, hash);
        }
        return sum;
    }
}
