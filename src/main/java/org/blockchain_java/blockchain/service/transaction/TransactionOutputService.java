package org.blockchain_java.blockchain.service.transaction;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.leveldb.service.LevelDBService;
import org.iq80.leveldb.DB;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionOutputService {
    private final DB levelDB;
    private final Gson gson;

    public List<TransactionOutput> createOutputs(String fromAddress,
                                                 String toAddress,
                                                 BigDecimal amount,
                                                 List<TransactionInput> transactionInputs){
        BigDecimal amountInInputs = transactionInputs.stream()
                .map(TransactionInput::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<TransactionOutput> transactionOutputs = new ArrayList<>();

        for(TransactionInput transactionInput : transactionInputs){
            String key = transactionInput.getHash()+":"+transactionInput.getOutput();
            if(levelDB.get(key.getBytes()) != null){
                TransactionOutput transactionOutput = gson.fromJson(new String(levelDB.get(key.getBytes())), TransactionOutput.class);
                transactionOutput.setSpent(true);
                levelDB.put(key.getBytes(), gson.toJson(transactionOutput).getBytes());
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
