package org.blockchain_java.blockchain.service.transaction;

import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionInputService {

    public List<TransactionInput> createInputs(String fromAddress, BigDecimal amount, Map<String, TransactionOutput> UTXO){
        List<TransactionInput> transactionInputs = new ArrayList<>();
        BigDecimal amountInUTXO  = BigDecimal.ZERO;

        for(Map.Entry<String, TransactionOutput> entry : UTXO.entrySet()){
            //Второе условие для того чтоб нельзя было до создания блока воспользоваться еще раз теми же outputs
            if(entry.getValue().getAddress().equals(fromAddress) && !entry.getValue().getSpent()){
                String[] dataOfOutput = entry.getKey().split(":");
                TransactionInput transactionInput = new TransactionInput(dataOfOutput[0], Long.valueOf(dataOfOutput[1]), fromAddress, entry.getValue().getAmount());
                amountInUTXO = amountInUTXO.add(entry.getValue().getAmount());
                transactionInputs.add(transactionInput);
                if(amountInUTXO.compareTo(amount) >= 0){
                    return transactionInputs;
                }
            }
        }
        return new ArrayList<>();
    }

    public List<TransactionInput> findAllOutputs(String fromAddress, BigDecimal amount){
        return new ArrayList<>();
    }
}
