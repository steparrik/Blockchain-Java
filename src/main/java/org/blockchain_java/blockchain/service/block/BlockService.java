package org.blockchain_java.blockchain.service.block;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.service.DbService;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    public void generateBlock(List<Transaction> transactions){
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
        }

        addNewBlock(newBlock
        );
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
}
