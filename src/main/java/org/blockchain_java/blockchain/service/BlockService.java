package org.blockchain_java.blockchain.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.utxo.UTXOService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlockService {
    private final UTXOService utxoService;

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

    public Block createBlock(List<Block> chain, List<Transaction> transactions){
        Block newBlock;

        for(Transaction transaction : transactions){
            utxoService.removeSpentOutput(transaction);
            utxoService.addNewOutput(transaction);
        }

        if(chain.isEmpty()){
            newBlock = createGenesisBlock(transactions);
        }else{
            newBlock = new Block();
            Block lastBlockInChain = chain.get(chain.size()-1);
            String previousHash = lastBlockInChain.getHash();
            long index = lastBlockInChain.getIndex();
            newBlock.setNonce(0);
            newBlock.setTransactions(transactions);
            newBlock.setHash("0");
            newBlock.setPreviousHash(previousHash);
            newBlock.setIndex(index+1);

        }
        System.out.println(newBlock);

        return newBlock;
    }

    public Block createGenesisBlock(List<Transaction> transactions){
        Block block = new Block();
        block.setIndex(0);
        block.setPreviousHash("0");
        block.setHash("0");
        block.setTransactions(transactions);
        block.setNonce(0);

        return block;
    }

}
