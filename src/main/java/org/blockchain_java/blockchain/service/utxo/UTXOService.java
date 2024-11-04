package org.blockchain_java.blockchain.service.utxo;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.iq80.leveldb.DB;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UTXOService {
    private final DB levelDB;
    private final Gson gson;


    public void removeSpentOutput(Transaction transaction){
        for(TransactionInput transactionInput : transaction.getInputs()){
            levelDB.delete((transactionInput.getHash()+":"+transactionInput.getOutput()).getBytes());
        }
    }


    public void addNewOutput(Transaction transaction){
        String hash = transaction.getHash();

        for(int i = 0;i<transaction.getOutputs().size();i++){
            String key = hash+":"+i;
            TransactionOutput transactionOutput = new TransactionOutput(transaction.getOutputs().get(i).getAddress(),
                    transaction.getOutputs().get(i).getAmount(), false);
            levelDB.put(key.getBytes(), gson.toJson(transactionOutput).getBytes());
        }
    }
}
