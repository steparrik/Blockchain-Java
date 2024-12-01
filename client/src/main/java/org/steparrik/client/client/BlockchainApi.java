package org.steparrik.client.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.steparrik.client.models.Block;
import org.steparrik.client.models.transaction.Transaction;
import org.steparrik.client.models.transaction.TransactionOutput;

import java.math.BigDecimal;
import java.util.List;


@FeignClient(name = "blockchain", url = "http://localhost:8080/v1/blockchain/api/")
public interface BlockchainApi {
    @GetMapping
    List<Block> getAllBlockchain(@RequestParam(required = false) String address);

    @GetMapping("/utxo/{address}")
    List<TransactionOutput> getOutputs(@PathVariable String address, @RequestParam(required = true) BigDecimal amount);

    @PostMapping
    void sendTransaction(@RequestBody Transaction transaction);

}
