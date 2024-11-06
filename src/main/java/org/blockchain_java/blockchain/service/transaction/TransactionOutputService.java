package org.blockchain_java.blockchain.service.transaction;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.iq80.leveldb.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionOutputService {
    private final UtxoService utxoService;
    private final Gson gson;

    @Autowired
    public TransactionOutputService(UtxoService utxoService, Gson gson) {
        this.utxoService = utxoService;
        this.gson = gson;
    }

    public List<TransactionOutput> createOutputs(String fromAddress,
                                                 String toAddress,
                                                 BigDecimal amount,
                                                 List<TransactionInput> transactionInputs){
        BigDecimal amountInInputs = transactionInputs.stream()
                .map(TransactionInput::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<TransactionOutput> transactionOutputs = new ArrayList<>();

        for(TransactionInput transactionInput : transactionInputs){
            String key = transactionInput.getHash()+":"+transactionInput.getOutput();
            if(utxoService.get(key) != null){
                TransactionOutput transactionOutput = gson.fromJson(new String(utxoService.get(key)), TransactionOutput.class);
                transactionOutput.setSpent(true);
                utxoService.put(key, gson.toJson(transactionOutput));
            }
        }

        TransactionOutput transactionOutputTo = new TransactionOutput(toAddress, amount, false);
        amountInInputs = amountInInputs.subtract(amount);
        TransactionOutput transactionOutputFrom = new TransactionOutput(fromAddress, amountInInputs, false);
        transactionOutputs.add(transactionOutputTo);
        transactionOutputs.add(transactionOutputFrom);

        return transactionOutputs;
    }


}
