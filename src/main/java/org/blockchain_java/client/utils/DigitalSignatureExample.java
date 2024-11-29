package org.blockchain_java.client.utils;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DigitalSignatureExample {

    // Метод для преобразования строки (Base64) обратно в публичный ключ
    public static PublicKey base64ToPublicKey(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePublic(keySpec);
    }
    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    // Метод для преобразования строки (Base64) обратно в приватный ключ
    public static PrivateKey base64ToPrivateKey(String base64PrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePrivate(keySpec);
    }
    public static void main(String[] args) {
        try {
            // Шаг 1: Генерация пары ключей (публичного и приватного)
            KeyPair keyPair = generateKeyPair();


            // Шаг 2: Подписание данных с использованием приватного ключа
            String data = "Hello, this is a sensitive message!";
            String signature = signData(data, keyPair.getPrivate());

            System.out.println(signature);
            System.out.println(keyToBase64(keyPair.getPublic()));

            // Шаг 3: Проверка подписи с использованием публичного ключа
            boolean isVerified = verifySignature(data, signature, keyPair.getPublic());

            // Вывод результата
            if (isVerified) {
                System.out.println("Signature is valid!");
            } else {
                System.out.println("Signature is invalid!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для генерации пары публичного и приватного ключа
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);// Используем RSA с длиной ключа 2048 бит
        return keyPairGenerator.generateKeyPair();
    }

    // Метод для подписания данных
    public static String signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");  // Алгоритм подписи
        signature.initSign(privateKey);
        signature.update(data.getBytes());

        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);  // Кодируем подпись в Base64 для удобства отображения
    }

    // Метод для проверки подписи
    public static boolean verifySignature(String data, String signature, PublicKey publicKey) throws Exception {
        Signature signatureInstance = Signature.getInstance("SHA256withRSA");  // Тот же алгоритм, что и для подписания
        signatureInstance.initVerify(publicKey);
        signatureInstance.update(data.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(signature);  // Декодируем подпись из Base64

        return signatureInstance.verify(signatureBytes);  // Проверяем подпись
    }
}
