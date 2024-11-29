package org.blockchain_java.client.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerification {

    public static PublicKey base64ToPublicKey(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePublic(keySpec);
    }

    // Метод для проверки подписи
    public static boolean verifySignature(String data, String signature, PublicKey publicKey) throws Exception {
        Signature signatureInstance = Signature.getInstance("SHA256withRSA");  // Тот же алгоритм, что и для подписания
        signatureInstance.initVerify(publicKey);
        signatureInstance.update(data.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(signature);  // Декодируем подпись из Base64

        return signatureInstance.verify(signatureBytes);  // Проверяем подпись
    }

    public static void main(String[] args) throws Exception {
        String data = "Hello, this is a sensitive message!";
        KeyPair keyPair = KeyGeneration.generateKeyPair();  // Генерация пары ключей
        String signature = DigitalSignature.signData(data, keyPair.getPrivate());
// 0aPJaNnr9jEcftlNiYogcLrOMiBz9JH7gGp6/Nc2hKwQym95poaNGF5PjZw2OjrpxB7qwdaMn4OVHZg+w+GeZ8JcTA4J5Pv0mKmtTUXeHq0nyBQ9iEX7wTBRKwVGVpCAlbnkElvjSUIC7joLGMoB/kyHkn1uunpyqdkFz1V2vbb2xke6fsfP+wxD5n/7lfed0NBDOGTIWwHfpsDOZ8d9n4JOr9SfEESvNy5AbDzy3QtnKR/znCgWuCadkofwrhCFCNsjrFnvELWIjwhWc4hd0CsEuvcF5fJTfBLmyGbFm5b7x3pNNx12WkBvT4cwTFUkW+7EaEnXDEMJ1cmay+lZxQ==
//MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3yRAuWz8pwHf395IyzvP3B11FTw7Xu0PIwQX8E6NhKCISceGpyFgiAc0A3XHPVIHV0EBj/5oQcIe1LuxfOodmaLYd/OINMieU38FbWkFZYOTlMHpRgKkmxQd7GK9fd7sIP6ynEPgxzhwY87PfZGbBXZQv+yzbgOzGgVK+b2XFRe7jBAUv9LozC1gcCQ2KlgubTVfKzvN0RwajidhTdzIeHOEHOSG+sw56WKIRsaDlp6KYN7L6vYj67IJudS8FILu72hJr+RyQGXHKH/95UxbsopWamHK0Vnk9NpmWbqyG/75DWHfogxUwB9G4Fdhlt3cj2wSefa5ZOvtiaE1AApu3QIDAQAB
        // Проверка подписи
        boolean isVerified = verifySignature(data, "0aPJaNnr9jEcftlNiYogcLrOMiBz9JH7gGp6/Nc2hKwQym95poaNGF5PjZw2OjrpxB7qwdaMn4OVHZg+w+GeZ8JcTA4J5Pv0mKmtTUXeHq0nyBQ9iEX7wTBRKwVGVpCAlbnkElvjSUIC7joLGMoB/kyHkn1uunpyqdkFz1V2vbb2xke6fsfP+wxD5n/7lfed0NBDOGTIWwHfpsDOZ8d9n4JOr9SfEESvNy5AbDzy3QtnKR/znCgWuCadkofwrhCFCNsjrFnvELWIjwhWc4hd0CsEuvcF5fJTfBLmyGbFm5b7x3pNNx12WkBvT4cwTFUkW+7EaEnXDEMJ1cmay+lZxQ==",
                base64ToPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3yRAuWz8pwHf395IyzvP3B11FTw7Xu0PIwQX8E6NhKCISceGpyFgiAc0A3XHPVIHV0EBj/5oQcIe1LuxfOodmaLYd/OINMieU38FbWkFZYOTlMHpRgKkmxQd7GK9fd7sIP6ynEPgxzhwY87PfZGbBXZQv+yzbgOzGgVK+b2XFRe7jBAUv9LozC1gcCQ2KlgubTVfKzvN0RwajidhTdzIeHOEHOSG+sw56WKIRsaDlp6KYN7L6vYj67IJudS8FILu72hJr+RyQGXHKH/95UxbsopWamHK0Vnk9NpmWbqyG/75DWHfogxUwB9G4Fdhlt3cj2wSefa5ZOvtiaE1AApu3QIDAQAB"));
        System.out.println(isVerified ? "Signature is valid!" : "Signature is invalid!");
    }
}
