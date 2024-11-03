package org.blockchain_java.blockchain.service;

import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.Block;
import org.springframework.stereotype.Service;

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
}
