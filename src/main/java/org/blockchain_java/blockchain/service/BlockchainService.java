package org.blockchain_java.blockchain.service;

import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.Block;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BlockchainService {
    private final BlockService blockService;

    public Block mineBlock(Block block, String difficultyPrefix) {
        block.setHash(blockService.calculateBlockHash(block));

        while (!block.getHash().startsWith(difficultyPrefix)) {
            long currentNonce = block.getNonce();
            block.setNonce(currentNonce + 1);
            block.setHash(blockService.calculateBlockHash(block));
        }
        return block;
    }

    public Block createGenesisBlock(){
        Block block = new Block();
        block.setIndex(0);
        block.setPreviousHash("0");
        block.setHash("0");
        block.setTransactions(new ArrayList<>());
        block.setNonce(0);

        return block;
    }
}
