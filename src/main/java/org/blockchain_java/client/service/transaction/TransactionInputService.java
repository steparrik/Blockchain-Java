package org.blockchain_java.client.service.transaction;

import com.google.gson.Gson;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionInputService {
    private final UtxoService utxoService;
    private final Gson gson;
    private final WitnessService witnessService;

    @Autowired
    public TransactionInputService(UtxoService utxoService, Gson gson, WitnessService witnessService) {
        this.utxoService = utxoService;
        this.gson = gson;
        this.witnessService = witnessService;
    }

    public List<TransactionInput> createInputs(String fromAddress, BigDecimal amount, String pubKey, String privateKey){
        List<TransactionOutput> transactionOutputs = utxoService.getOutputs(fromAddress, amount);
        System.out.println(transactionOutputs);
        if(!transactionOutputs.isEmpty()){
            List<TransactionInput> transactionInputs = transactionOutputs.stream().map(trO -> {
                String[] dataOfKey = trO.getKey().split(":");
                TransactionInput transactionInput = new TransactionInput(dataOfKey[0], Long.valueOf(dataOfKey[1]),
                        trO.getAddress(), witnessService.generateWitness(privateKey, pubKey, dataOfKey[0], trO.getAddress(), trO.getAmount()), trO.getAmount());
                return transactionInput;
            }).collect(Collectors.toCollection(ArrayList::new));
            System.out.println(transactionInputs);
            return transactionInputs;
        }
        return null;
    }
}
