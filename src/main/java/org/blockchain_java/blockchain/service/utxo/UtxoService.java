package org.blockchain_java.blockchain.service.utxo;

import com.google.gson.Gson;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.DbService;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UtxoService implements DbService {
    private final DB utxoDb;
    private final Gson gson;

    @Autowired
    public UtxoService(@Qualifier("utxoDb") DB utxoDb, Gson gson){
        this.utxoDb = utxoDb;
        this.gson = gson;
    }

    @Override
    public void put(String key, String value) {
        utxoDb.put(key.getBytes(), value.getBytes());
    }

    @Override
    public String get(String key) {
        byte[] value = utxoDb.get(key.getBytes());
        return value != null ? new String(value) : null;
    }

    @Override
    public void delete(String key) {
        utxoDb.delete(key.getBytes());
    }

    @Override
    public DBIterator iterator() {
        return utxoDb.iterator();
    }

    public void removeSpentOutput(Transaction transaction){
        for(TransactionInput transactionInput : transaction.getInputs()){
            utxoDb.delete((transactionInput.getHash()+":"+transactionInput.getOutput()).getBytes());
        }
    }


    public void addNewOutput(Transaction transaction){
        String hash = transaction.getHash();

        for(int i = 0;i<transaction.getOutputs().size();i++){
            String key = hash+":"+i;
            TransactionOutput transactionOutput = new TransactionOutput(transaction.getOutputs().get(i).getAddress(),
                    transaction.getOutputs().get(i).getAmount(), false);
            utxoDb.put(key.getBytes(), gson.toJson(transactionOutput).getBytes());
        }
    }
}
