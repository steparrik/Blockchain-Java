package org.steparrik.blockchain.api;

import ch.qos.logback.core.model.INamedModel;
import com.google.gson.Gson;
import org.steparrik.blockchain.models.Block;
import org.steparrik.blockchain.models.transaction.Transaction;
import org.steparrik.blockchain.models.transaction.TransactionOutput;
import org.steparrik.blockchain.service.block.BlockService;
import org.steparrik.blockchain.service.mempool.MempoolService;
import org.steparrik.blockchain.service.utxo.UtxoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.steparrik.blockchain.tcp.TcpClient;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("v1/blockchain/api")
public class BlockchainController {
    private final BlockService blockService;
    private final UtxoService utxoService;
    private final Gson gson;
    private final MempoolService mempoolService;

    @Autowired
    public BlockchainController(BlockService blockService, UtxoService utxoService, Gson gson, MempoolService mempoolService) {
        this.blockService = blockService;
        this.utxoService = utxoService;
        this.gson = gson;
        this.mempoolService = mempoolService;
    }

    @GetMapping
    public List<Block> getAllBlockchain(){
        return blockService.getBlockchain();
    }

    @GetMapping("/utxo/{address}")
    public List<TransactionOutput> getOutputs(@PathVariable String address, @RequestParam(required = true) BigDecimal amount){
        return utxoService.getOutputs(address, amount);
    }

    @PostMapping("/transaction")
    public void sendTransaction(@RequestBody Transaction transaction)  {
        mempoolService.addTransaction(transaction);
    }

    @PostMapping("/add-test-utxo")
    public void addTestUtxo(@RequestParam(required = true) String address){
        utxoService.put("hash:0", gson.toJson(new TransactionOutput(address, new BigDecimal(1000), false, null)));
    }

    @PostMapping("/mine")
    public void mineBlock(){
        Map<String, Transaction> mempool = mempoolService.getMempool();
        List<Transaction> transactions = new ArrayList<>(mempool.values());
        blockService.generateBlock(transactions);
    }

    @GetMapping("/testMessage")
    public void sendTransaction(@RequestParam String message)  {
        List<String> list = new ArrayList<>();
        list.add("127.0.0.1:9001");
        list.add("127.0.0.1:9002");
        TcpClient tcpClient = new TcpClient(list);
        tcpClient.sendMessageToAll(message);
    }

}
