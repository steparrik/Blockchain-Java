package org.blockchain_java.blockchain.service.block;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.service.DbService;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Service
public class BlockService implements DbService {
    private final DB blockchainDb;
    private final UtxoService utxoService;
    private final Gson gson;

    @Autowired
    public BlockService(@Qualifier("blockchainDb") DB blockchainDb, UtxoService utxoService, Gson gson){
        this.blockchainDb = blockchainDb;
        this.utxoService = utxoService;
        this.gson = gson;
    }

    @Override
    public void put(String key, String value) {
        blockchainDb.put(key.getBytes(), value.getBytes());
    }

    public void addNewBlock(Block block){
        Block afterMineBlock = mineBlock(block, "000");
        put(afterMineBlock.getHash(), gson.toJson(afterMineBlock));
        put("last", afterMineBlock.getHash());
    }

    @Override
    public String get(String key) {
        byte[] value = blockchainDb.get(key.getBytes());
        return value != null ? new String(value) : null;
    }

    @Override
    public void delete(String key) {
        blockchainDb.delete(key.getBytes());
    }

    @Override
    public DBIterator iterator() {
        return blockchainDb.iterator();
    }

    @SneakyThrows
    public String calculateBlockHash(Block block) {
        String dataToHash = block.getPreviousHash()
                + Long.toString(block.getTimestamp())
                + Long.toString(block.getNonce())
                + Long.toString(block.getIndex())
                + block.getTransactions().toString();
        MessageDigest digest = null;
        byte[] bytes = null;
        digest = MessageDigest.getInstance("SHA-256");
        bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append(String.format("%02x", b));
        }

        return buffer.toString();
    }

    @SneakyThrows
    public static PublicKey base64ToPublicKey(String base64PublicKey) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public Block generateGenesisBlock(List<Transaction> transactions){
        Block block = new Block();
        block.setIndex(0);
        block.setPreviousHash("0");
        block.setHash("0");
        block.setTransactions(transactions);
        block.setNonce(0);

        return block;
    }

    public Block mineBlock(Block block, String difficultyPrefix) {
        block.setHash(calculateBlockHash(block));

        while (!block.getHash().startsWith(difficultyPrefix)) {
            long currentNonce = block.getNonce();
            block.setNonce(currentNonce + 1);
            block.setHash(calculateBlockHash(block));
        }
        return block;
    }

    public static String publicKeyToAddress(String publicKey) {
        byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(publicKey);

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
//
//    public List<String> generateWitness(String privateKey, String pubKey, String transactionHash, String address, BigDecimal amount){
//        List<String> witness = new ArrayList<>(2);
//        witness.add(pubKey);
//        witness.add(signData(transactionHash + address + amount, privateKey));
//        return witness;
//    }

    public void generateBlock(List<Transaction> transactions) throws Exception {
        Block newBlock;

        for(Transaction transaction : transactions){
            if (!validateTransaction(transaction.getInputs())) {
                throw new Exception("НАЕБЩИК ТЫ ЕБУЧИЙ");
            }
            utxoService.removeSpentOutput(transaction);
            utxoService.addNewOutput(transaction);
        }

        if(get("last") == null){
            newBlock = generateGenesisBlock(transactions);
        }else{
            newBlock = new Block();
            Block lastBlockInChain = gson.fromJson(get(get("last")), Block.class);
            String previousHash = lastBlockInChain.getHash();
            long index = lastBlockInChain.getIndex();
            newBlock.setNonce(0);
            newBlock.setTransactions(transactions);
            newBlock.setHash("0");
            newBlock.setPreviousHash(previousHash);
            newBlock.setIndex(index+1);
        }

        addNewBlock(newBlock
        );
    }

    public boolean validateTransaction(List<TransactionInput> transactionInputs) {
        for (TransactionInput transactionInput : transactionInputs) {
            System.out.println("ВАЛИДАЦИЯ: " + transactionInput.getAddress() + " " + publicKeyToAddress(transactionInput.getWitness().get(0)));
            System.out.println(transactionInput.getAddress().equals(publicKeyToAddress(transactionInput.getWitness().get(0))));
            if (!transactionInput.getAddress().equals(publicKeyToAddress(transactionInput.getWitness().get(0)))) {
                return false;
            }
            String data = transactionInput.getTransactionHash() + transactionInput.getAddress() + transactionInput.getAmount();
            if (!verifySignature(data, transactionInput.getWitness().get(1), transactionInput.getWitness().get(0))) {
                return false;
            }
        }
        return true;
    }


}
