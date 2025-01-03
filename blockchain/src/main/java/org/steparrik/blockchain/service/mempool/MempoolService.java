package org.steparrik.blockchain.service.mempool;

import lombok.SneakyThrows;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.steparrik.blockchain.models.transaction.Transaction;
import org.steparrik.blockchain.models.transaction.TransactionInput;
import org.steparrik.blockchain.service.utxo.UtxoService;
import org.steparrik.blockchain.utils.exception.ValidateTransactionException;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MempoolService {
    private final Map<String, Transaction> mempool = new ConcurrentHashMap<>();
    private final UtxoService utxoService;

    @Autowired
    public MempoolService(UtxoService utxoService) {
        this.utxoService = utxoService;
    }

    public void addTransaction(Transaction transaction) {
        validateTransaction(transaction.getInputs());
        mempool.put(transaction.getHash(), transaction);
    }

    public Map<String, Transaction> getMempool() {
        return mempool;
    }

    public void removeTransactions(Iterable<String> txIds) {
        for (String txId : txIds) {
            mempool.remove(txId);
        }
    }

    public void validateTransaction(List<TransactionInput> transactionInputs) {
        for (TransactionInput transactionInput : transactionInputs) {
            String keyForOutput = transactionInput.getTransactionHash() + ":" + transactionInput.getOutput();
            if(utxoService.get(keyForOutput) == null){
                throw new ValidateTransactionException("Transaction verification error: The input does not exist", HttpStatus.BAD_REQUEST);
            }
            if (!transactionInput.getAddress().equals(publicKeyToAddress(transactionInput.getWitness().get(0)))) {
                throw new ValidateTransactionException("Transaction verification error: The sender's address does not match your public key.", HttpStatus.BAD_REQUEST);
            }
            String data = transactionInput.getTransactionHash() + transactionInput.getAddress() + transactionInput.getAmount();
            if (!verifySignature(data, transactionInput.getWitness().get(1), transactionInput.getWitness().get(0))) {
                throw new ValidateTransactionException("Transaction verification error: The signature is incorrect", HttpStatus.BAD_REQUEST);
            }
        }
    }

    public static String publicKeyToAddress(String publicKey) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);

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

    @SneakyThrows
    public static boolean verifySignature(String data, String signature, String publicKeyStr) {
        PublicKey publicKey = base64ToPublicKey(publicKeyStr);
        Signature signatureInstance = Signature.getInstance("SHA256withRSA");
        signatureInstance.initVerify(publicKey);
        signatureInstance.update(data.getBytes());

        byte[] signatureBytes = Base64.getDecoder().decode(signature);

        return signatureInstance.verify(signatureBytes);
    }

    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String bytesToHex(byte[] bytes) {
        return Hex.toHexString(bytes);
    }

    @SneakyThrows
    public static PublicKey base64ToPublicKey(String base64PublicKey) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

}
