package org.steparrik.blockchain.service.block;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.steparrik.blockchain.models.Block;
import org.steparrik.blockchain.models.DataType;
import org.steparrik.blockchain.models.transaction.Transaction;
import org.steparrik.blockchain.models.transaction.TransactionInput;
import org.steparrik.blockchain.service.DbService;
import org.steparrik.blockchain.service.mempool.MempoolService;
import org.steparrik.blockchain.service.utxo.UtxoService;
import org.steparrik.blockchain.tcp.TcpClient;
import org.steparrik.blockchain.utils.exception.ValidateTransactionException;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Service
public class BlockService implements DbService {
    private final DB blockchainDb;
    private final UtxoService utxoService;
    private final Gson gson;
    private final MempoolService mempoolService;
    private final TcpClient tcpClient;

    @Autowired
    public BlockService(@Qualifier("blockchainDb") DB blockchainDb, UtxoService utxoService, Gson gson, MempoolService mempoolService){
        this.blockchainDb = blockchainDb;
        this.utxoService = utxoService;
        this.gson = gson;
        this.mempoolService = mempoolService;
        this.tcpClient = new TcpClient(List.of("blockchain1:9000", "blockchain2:9000", "blockchain3:9000", "blockchain4:9000", "blockchain5:9000"));

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
                + block.getTimestamp()
                + block.getNonce()
                + block.getIndex()
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


    public Block generateGenesisBlock(List<Transaction> transactions){
        Block block = new Block();
        block.setType(DataType.BLOCK);
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

    public List<Block> getBlockchain(){
        byte[] bytesOfLastBlockHash = blockchainDb.get("last".getBytes());
        if(bytesOfLastBlockHash == null){
            return new LinkedList<>();
        }

        String lastBlockHash = gson.fromJson(new String(bytesOfLastBlockHash), String.class);
        List<Block> blockchain = new LinkedList<>();
        Block block = gson.fromJson(new String(blockchainDb.get(lastBlockHash.getBytes())), Block.class);

        while (!block.getPreviousHash().equals("0")){
            blockchain.add(block);
            block = gson.fromJson(new String(blockchainDb.get(block.getPreviousHash().getBytes())), Block.class);
        }

        blockchain.add(block);
        return blockchain;
    }

    @Async
    public void generateBlock(List<Transaction> transactions) {
        Block newBlock;

        for(Transaction transaction : transactions){
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
            newBlock.setType(DataType.BLOCK);
        }

        addNewBlock(newBlock);

        mempoolService.removeTransactions(transactions.stream().map(Transaction::getHash).toList());
        tcpClient.sendTransactionToAll(gson.toJson(newBlock));
    }



}
