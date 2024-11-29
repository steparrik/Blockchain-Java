package org.blockchain_java.client.utils;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyGeneration {
    // Метод для генерации пары публичного и приватного ключа
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Используем RSA с длиной ключа 2048 бит
        return keyPairGenerator.generateKeyPair();
    }
    /*Public Key: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsQEiby92PcQVGW5rO2zqUVm11vvKK+TAr6C1h2e6QEnhdjQ9smESHXLJ+nmOJT2KukLrk+IcDlpbE0hquMdvykrCGnATRLC+ULF3sYLYtzlXb8dptDWHyEtvA/QIbGqdxeSI34ronjPbjrr3DAZTKrJK22MWmdThYBKG4daon1LwQoyGtzfJMcVDW924DPN1HE+1rVvXMgGEV4SOANCYwggw1jbaRsOnbGwv2fiS5v2J35Nf3/dug8cVwQFY3Qk1WfxAN6gASSYrDACgiSBwW1cnue8G9d2myeGPB1eAO0irjAbAIdoLzqGIBR5HAwTZVrmNOi9N1wvne3rs8MOM1wIDAQAB
Private Key: MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCxASJvL3Y9xBUZbms7bOpRWbXW+8or5MCvoLWHZ7pASeF2ND2yYRIdcsn6eY4lPYq6QuuT4hwOWlsTSGq4x2/KSsIacBNEsL5QsXexgti3OVdvx2m0NYfIS28D9Ahsap3F5IjfiuieM9uOuvcMBlMqskrbYxaZ1OFgEobh1qifUvBCjIa3N8kxxUNb3bgM83UcT7WtW9cyAYRXhI4A0JjCCDDWNtpGw6dsbC/Z+JLm/Ynfk1/f926DxxXBAVjdCTVZ/EA3qABJJisMAKCJIHBbVye57wb13abJ4Y8HV4A7SKuMBsAh2gvOoYgFHkcDBNlWuY06L03XC+d7euzww4zXAgMBAAECggEAO9hUWh9D7RI0yAprOwZSGvsn3e33puuKsla1yXgqor3X+u0ixwLG6yn6XDNo+fJayysBF1jlf3zROi3L13mmtFCeW+30tc7EKeg2vc+tkhVTGLnlZUaMCA5pFH8XsgBCI7xnAp2mlIaX/yP7jxc8Wz04zgAsjgpJwP/CluzKykLiGIw/qCPIV/Bw+wlIrMMmdJOau8RCO73ASYti1EzDX2XAv/edNOUVhKRAOv+GqRa1s1Sk9zHwG/frFM3tlDs75SHeE1owO/mHc5wwwPbbrW+pellbbfBfEGiPX5HKqtnAqsPZdTCrY/u250aEUgOxsSN2VCA+cTe19gCkOHylEQKBgQDlsDsWfhbvTqIDO6cM1hP/LNMTpOyryi64GgLxm9XO1aku1YYsuzLdZJPNgMB+fN7VS239dV0Hizt51frdxVNJuOBM9hDLailhI0SRfTwe51h2D43vAPfubKyACFw4yCytqVKeHishUofMXb/E/xka8hD516uX4nGpLZ4D1uKDAwKBgQDFR+kKIgcshTMXU0bHDrcgEuiqRCdgdW756ErSB2bi1KChHoRMoSTaRyMrtawOffnXSNwzrFrGMetOId6Aba56hHWjA/BPoHaXVpyJc+xOQs7mDGlkN5X3k0IjuKd30+wBKSZ2bz5s6jzeQZ0V8FN/HgH7ZKgkwjpcF5QBjuG8nQKBgQCCCQ66wjHb08m3SNtfUgfKLKdndLSk4XcntVo7TP1z9tSsdihbRLQcUSe4bY8NTUaevPCNUReVAMdwR6uaSG/Jygf6qkiB0R2SJ/RW5e7WlCUp2c6CJCs5aX8TkSgc4X8h5j2m1O+YEvBczCqYrc+ZyLfBqs8z0viEThYqmEpu3wKBgQCWaKMF9Lefe9ZeHEHEFjYMIB5/YgLbie0YX8BbrQUGP7JP5OJdQibEvKPV+Wa6jdVkC8rHIsr6fKi4Ix5usDDse/zsUl5vdxmzfHdQXK93Y3w27JOVssOFxCIS9f/IXix7HhKTDXF8vIG3ujK3vUkLi6dbMXz4uZShDeDr5zG4CQKBgBVlmxkOxM2WQK8flCalR+vjrilDrTcB+dSmwhX/3cnRqRWdaOvZtjOlWn7dpHKtgfr/OblaDWyQVnGY+Seud7NWMLZxIN0MEO4JOj2FQsJhx0SySvbO/VRXNB+vPYRUAS3zqcmHhGemw2vSl8ZytXXnOmgRs4Q6iEBgCIF/Z3+l
*/
    // Метод для получения ключа в виде строки (Base64)
    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
    // Метод для преобразования строки (Base64) обратно в публичный ключ
    public static PublicKey base64ToPublicKey(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePublic(keySpec);
    }

    // Метод для преобразования строки (Base64) обратно в приватный ключ
    public static PrivateKey base64ToPrivateKey(String base64PrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePrivate(keySpec);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        System.out.println(generateKeyPair().getPublic());
        String publicKeyStr = keyToBase64(keyPair.getPublic());
        String privateKeyStr = keyToBase64(keyPair.getPrivate());

        // Выводим публичный и приватный ключи в строковом представлении
        System.out.println("Public Key: " + publicKeyStr);
        System.out.println("Private Key: " + privateKeyStr);

        try {
            PublicKey publicKey = base64ToPublicKey(publicKeyStr);
            String publicKeyStr2 = keyToBase64(publicKey);

            System.out.println("Public Key: " + publicKeyStr2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
