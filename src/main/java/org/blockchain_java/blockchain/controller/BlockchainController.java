package org.blockchain_java.blockchain.controller;

import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.block.BlockService;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("v1/blockchain/api")
public class BlockchainController {
    private final BlockService blockService;
    private final UtxoService utxoService;

    @Autowired
    public BlockchainController(BlockService blockService, UtxoService utxoService) {
        this.blockService = blockService;
        this.utxoService = utxoService;
    }

    @GetMapping
    public List<Block> getAllBlockchain(){
        return blockService.getBlockchain();
    }

    @GetMapping("/utxo/{address}")
    public List<TransactionOutput> getOutputs(@PathVariable String address, @RequestParam(required = true) BigDecimal amount){
        return utxoService.getOutputs(address, amount);
    }

    @PostMapping
    public void sendTransaction(@RequestBody Transaction transaction) throws Exception {
        blockService.generateBlock(Collections.singletonList(transaction));
    }

}
