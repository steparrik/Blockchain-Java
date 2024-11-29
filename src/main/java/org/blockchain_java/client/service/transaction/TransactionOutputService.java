package org.blockchain_java.client.service.transaction;

import com.google.gson.Gson;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.springframework.beans.factory.annotation.Autowired;
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
//        for(TransactionInput transactionInput : transactionInputs){
//            String key = transactionInput.getTransactionHash()+":"+transactionInput.getOutput();
//            if(utxoService.get(key) != null){
//                TransactionOutput transactionOutput = gson.fromJson(new String(utxoService.get(key)), TransactionOutput.class);
//                transactionOutput.setSpent(true);
//                utxoService.put(key, gson.toJson(transactionOutput));
//            }
//        } Позже посмторю так как эта часть нужна для работы нескольких нод, spent - флаг что нельзя трогать выходы, но так как сейчас проверка в блокчейне возмржно не надо будет

        TransactionOutput transactionOutputTo = new TransactionOutput(toAddress, amount, false, null);
        amountInInputs = amountInInputs.subtract(amount);
        TransactionOutput transactionOutputFrom = new TransactionOutput(fromAddress, amountInInputs, false, null);
        transactionOutputs.add(transactionOutputTo);
        transactionOutputs.add(transactionOutputFrom);

        return transactionOutputs;
    }


}
