package org.blockchain_java.client.utils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

public class DigitalSignature {

    // Метод для подписания данных
    public static String signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA"); // Алгоритм подписи
        signature.initSign(privateKey);
        signature.update(data.getBytes());

        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);  // Кодируем подпись в Base64 для удобства отображения
    }

    public static void main(String[] args) throws Exception {
        // Пример подписи
        String data = "Hello, this is a sensitive message!";
        KeyPair keyPair = KeyGeneration.generateKeyPair();  // Генерация пары ключей

        String signature = signData(data, keyPair.getPrivate());

        System.out.println("Signed data: " + signature);
    }
}
