package com.makolab.utils;

import java.util.Collection;

public class ByteArrayUtils {
    public static String ToHexString(byte[] bytes)
    {
        char[] c = new char[bytes.length * 2];
        int b;
        for (int i = 0; i < bytes.length; i++)
        {
            b = bytes[i] >> 4;
            c[i * 2] = (char)(87 + b + (((b - 10) >> 31) & -39));
            b = bytes[i] & 0xF;
            c[i * 2 + 1] = (char)(87 + b + (((b - 10) >> 31) & -39));
        }
        return new String(c);
    }

    public static byte[] AddHashes(byte[] a, byte[] b)
    {
        if (a.length != b.length)
        {
            throw new IllegalArgumentException("Array lenghts must be the same.");
        }
        short sum = 0;
        var result = new byte[a.length];
        for (var i = a.length - 1; i >= 0; i--)
        {
            sum += (short)(a[i] + b[i]);
            result[i] = (byte)(sum & 0x00FF);
            sum >>= 8;
        }
        return result;
    }

    public static byte[] AddHashes(Collection<byte[]> hashes)
    {
        if (hashes.isEmpty())
        {
            throw new IllegalArgumentException("Hash collection is empty");
        }
        byte[] sum = new byte[hashes.iterator().next().length];
        for (var hash : hashes)
        {
            sum = AddHashes(sum, hash);
        }
        return sum;
    }
}
