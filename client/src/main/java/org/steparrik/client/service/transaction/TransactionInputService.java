package org.steparrik.client.service.transaction;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.steparrik.client.client.BlockchainApi;
import org.steparrik.client.models.transaction.TransactionInput;
import org.steparrik.client.models.transaction.TransactionOutput;
import org.steparrik.client.utils.exception.ValidateTransactionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionInputService {
    private final Gson gson;
    private final BlockchainApi blockchainApi;
    private final WitnessService witnessService;

    @Autowired
    public TransactionInputService(Gson gson, WitnessService witnessService, BlockchainApi blockchainApi) {
        this.gson = gson;
        this.witnessService = witnessService;
        this.blockchainApi = blockchainApi;
    }

    public List<TransactionInput> createInputs(String fromAddress, BigDecimal amount, String pubKey, String privateKey){
        List<TransactionOutput> transactionOutputs = blockchainApi.getOutputs(fromAddress, amount);
        if(!transactionOutputs.isEmpty()){
            List<TransactionInput> transactionInputs = transactionOutputs.stream().map(trO -> {
                String[] dataOfKey = trO.getKey().split(":");
                TransactionInput transactionInput = new TransactionInput(dataOfKey[0], Long.valueOf(dataOfKey[1]),
                        trO.getAddress(), witnessService.generateWitness(privateKey, pubKey, dataOfKey[0], trO.getAddress(), trO.getAmount()), trO.getAmount());
                return transactionInput;
            }).collect(Collectors.toCollection(ArrayList::new));
            return transactionInputs;
        }
        return new ArrayList<>();
    }
}
