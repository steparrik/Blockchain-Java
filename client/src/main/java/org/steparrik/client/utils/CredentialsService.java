package org.steparrik.client.utils;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;

import java.security.*;
import java.util.Base64;

import static java.util.Base64.getDecoder;

@Component
public class CredentialsService {

    @SneakyThrows
    public  KeyPair generateKeyPair() {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public  String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public String publicKeyToAddress(String publicKey) {
        byte[] publicKeyBytes = getDecoder().decode(publicKey);

        SHA256Digest sha256Digest = new SHA256Digest();
        byte[] sha256Hash = new byte[sha256Digest.getDigestSize()];
        sha256Digest.update(publicKeyBytes, 0, publicKeyBytes.length);
        sha256Digest.doFinal(sha256Hash, 0);

        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        byte[] ripemd160Hash = new byte[ripemd160Digest.getDigestSize()];
        ripemd160Digest.update(sha256Hash, 0, sha256Hash.length);
        ripemd160Digest.doFinal(ripemd160Hash, 0);

        return bytesToHex(ripemd160Hash);
    }

    public String bytesToHex(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

}
