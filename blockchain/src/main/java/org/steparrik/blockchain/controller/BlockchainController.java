package org.steparrik.blockchain.controller;

import com.google.gson.Gson;
import org.steparrik.blockchain.models.Block;
import org.steparrik.blockchain.models.transaction.Transaction;
import org.steparrik.blockchain.models.transaction.TransactionOutput;
import org.steparrik.blockchain.service.block.BlockService;
import org.steparrik.blockchain.service.utxo.UtxoService;
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
    private final Gson gson;

    @Autowired
    public BlockchainController(BlockService blockService, UtxoService utxoService, Gson gson) {
        this.blockService = blockService;
        this.utxoService = utxoService;
        this.gson = gson;
    }

    @GetMapping
    public List<Block> getAllBlockchain(@RequestParam(required = false) String address){
        utxoService.put("hash:0", gson.toJson(new TransactionOutput(address, new BigDecimal(1000), false, null)));
        return blockService.getBlockchain();
    }

    @GetMapping("/utxo/{address}")
    public List<TransactionOutput> getOutputs(@PathVariable String address, @RequestParam(required = true) BigDecimal amount){
        return utxoService.getOutputs(address, amount);
    }

    @PostMapping
    public void sendTransaction(@RequestBody Transaction transaction)  {
        blockService.generateBlock(Collections.singletonList(transaction));
    }

}
