package org.steparrik.client.service.transaction;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.steparrik.client.client.BlockchainApi;
import org.steparrik.client.models.transaction.TransactionInput;
import org.steparrik.client.models.transaction.TransactionOutput;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionOutputService {
    private final Gson gson;
    private final BlockchainApi blockchainApi;

    @Autowired
    public TransactionOutputService(BlockchainApi blockchainApi, Gson gson) {
        this.blockchainApi = blockchainApi;
        this.gson = gson;
    }

    public List<TransactionOutput> createOutputs(String fromAddress,
                                                 String toAddress,
                                                 BigDecimal amount,
                                                 List<TransactionInput> transactionInputs){
        BigDecimal amountInInputs = transactionInputs.stream()
                .map(TransactionInput::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<TransactionOutput> transactionOutputs = new ArrayList<>();

        TransactionOutput transactionOutputTo = new TransactionOutput(toAddress, amount, false, null);
        amountInInputs = amountInInputs.subtract(amount);
        TransactionOutput transactionOutputFrom = new TransactionOutput(fromAddress, amountInInputs, false, null);
        transactionOutputs.add(transactionOutputTo);
        transactionOutputs.add(transactionOutputFrom);

        return transactionOutputs;
    }


}
