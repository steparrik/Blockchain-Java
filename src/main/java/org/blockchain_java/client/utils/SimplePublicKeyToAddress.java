package org.blockchain_java.client.utils;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;

public class SimplePublicKeyToAddress {

    public static String publicKeyToAddress(String publicKey) {
        // Шаг 1: Преобразуем публичный ключ из Base64 в байты
        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(publicKey);

        // Шаг 2: Хешируем публичный ключ с использованием SHA-256
        SHA256Digest sha256Digest = new SHA256Digest();
        byte[] sha256Hash = new byte[sha256Digest.getDigestSize()];
        sha256Digest.update(publicKeyBytes, 0, publicKeyBytes.length);
        sha256Digest.doFinal(sha256Hash, 0);

        // Шаг 3: Хешируем результат SHA-256 с использованием RIPEMD-160 (с использованием BouncyCastle)
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        byte[] ripemd160Hash = new byte[ripemd160Digest.getDigestSize()];
        ripemd160Digest.update(sha256Hash, 0, sha256Hash.length);
        ripemd160Digest.doFinal(ripemd160Hash, 0);

        // Преобразуем результат в строку в формате Hex
        return bytesToHex(ripemd160Hash);
    }

    // Метод для конвертации байтов в строку Hex
    public static String bytesToHex(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    public static void main(String[] args) {
        // Пример публичного ключа (в формате Base64)
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvrkf5S9HPkkxPy2YHWAHllvlmhStOAILxISjkZqNTFshKiQ+rl9m58Z+wTSSZJjLVMvlDPlZ5zJFPICoUdK2lGV4ALjVoY248GAggjwrXf7o1bz9OG2CDsSHsHTbFHMGgdMndGq5Xm6kCaRCUdjSvwlvszlB1Y31/mEfPMU68HAvyDSJ1OM9kaqVzKGXA34L5MzFDwFfdbmpPJ0m5AKFbgNSHX5CUfIH8CdzVaskZWSbQuTQanbVhXjw2mWg8uHsWAEV0rogGEOuhBWrP5mYQ9Fat6v4FNLEijOvOQ5SaWhPC7MRpeOJT6E3VC+QJL/jF1pXIxJ0Yb00G+CdzrIOiwIDAQAB";

        try {
            // Получаем адрес
            String address = publicKeyToAddress(publicKey);
            System.out.println("Generated address: " + address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
