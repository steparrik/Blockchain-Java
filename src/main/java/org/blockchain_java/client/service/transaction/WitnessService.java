package org.blockchain_java.client.service.transaction;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class WitnessService {
    public List<String> generateWitness(String privateKey, String pubKey, String transactionHash, String address, BigDecimal amount){
        List<String> witness = new ArrayList<>(2);
        witness.add(pubKey);
        witness.add(signData(transactionHash + address + amount, privateKey));
        return witness;
    }

    @SneakyThrows
    public String signData(String data, String privateKey)  {

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(base64ToPrivateKey(privateKey));
        signature.update(data.getBytes());

        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    @SneakyThrows
    public static PrivateKey base64ToPrivateKey(String base64PrivateKey) {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Используем RSA
        return keyFactory.generatePrivate(keySpec);
    }


}
