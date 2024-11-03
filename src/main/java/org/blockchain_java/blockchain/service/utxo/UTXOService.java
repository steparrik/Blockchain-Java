package org.blockchain_java.blockchain.service.utxo;

import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UTXOService {

    public void removeSpentOutput(Transaction transaction, Map<String, TransactionOutput> UTXO){
        for(TransactionInput transactionInput : transaction.getInputs()){
            UTXO.remove(transactionInput.getHash()+":"+transactionInput.getOutput());
        }
    }


    public void addNewOutput(Transaction transaction, Map<String, TransactionOutput> UTXO){
        String hash = transaction.getHash();

        for(int i = 0;i<transaction.getOutputs().size();i++){
            String key = hash+":"+i;
            TransactionOutput transactionOutput = new TransactionOutput(transaction.getOutputs().get(i).getAddress(),
                    transaction.getOutputs().get(i).getAmount(), false);
            UTXO.put(key, transactionOutput);
        }
    }
}
