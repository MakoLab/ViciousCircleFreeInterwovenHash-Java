package com.makolab.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class ByteArrayUtilsTest {
    @ParameterizedTest
    @CsvSource(value = {
            "test1:1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014",
            "test2:60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752",
            "test3:fd61a03af4f77d870fc21e05e7e80678095c92d808cfb3b5c279ee04c74aca13",
            "test4:a4e624d686e03ed2767c0abd85c14426b0b1157d2ce81d27bb4fe4f6f01d688a",
            "test5:a140c0c1eda2def2b830363ba362aa4d7d255c262960544821f556e16661b6ff"
    }, delimiter = ':')
    public void shouldGiveProperHexString(String input, String hashString) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");
        var arr = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        var str = ByteArrayUtils.toHexString(arr);
        assertEquals(hashString, str);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1b4f:27:79",
            "6030:96:48",
            "f9aa:-7:-86"
    }, delimiter = ':')
    public void shouldGiveProperByteArray(String input, int first, int second) {
        var arr = ByteArrayUtils.toByteArray(input);
        assertEquals(first, arr[0]);
        assertEquals(second, arr[1]);
    }

    @Test
    public void shouldThrowException_NotHexChar() {
        assertThrows(NumberFormatException.class, () -> {
            ByteArrayUtils.toByteArray("6j303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752");
        });
    }

    @ParameterizedTest
    @CsvSource(value = {
            "01010101:02020202",
            "fafafafa:f5f5f5f4",
            "7cd565c5:f9aacb8a"
    }, delimiter = ':')
    public void shouldAddTwoByteArrays(String addend, String expectedSum) {
        var arr1 = ByteArrayUtils.toByteArray(addend);
        var arr2 = ByteArrayUtils.toByteArray(addend);
        var sum = ByteArrayUtils.addHashes(arr1, arr2);
        assertArrayEquals(ByteArrayUtils.toByteArray(expectedSum), sum);
    }
}