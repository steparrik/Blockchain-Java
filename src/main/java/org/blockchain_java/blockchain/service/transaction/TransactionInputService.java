package org.blockchain_java.blockchain.service.transaction;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionInputService {
    private final UtxoService utxoService;
    private final Gson gson;

    @Autowired
    public TransactionInputService(UtxoService utxoService, Gson gson) {
        this.utxoService = utxoService;
        this.gson = gson;
    }

    public List<TransactionInput> createInputs(String fromAddress, BigDecimal amount){
        List<TransactionInput> transactionInputs = new ArrayList<>();
        BigDecimal amountInUTXO  = BigDecimal.ZERO;

        DBIterator iterator = utxoService.iterator();
        iterator.seekToFirst();

        while (iterator.hasNext()){
            Map.Entry<byte[], byte[]> entry = iterator.next();
            String key = new String(entry.getKey());
            TransactionOutput transactionOutput = gson.fromJson(new String(entry.getValue()), TransactionOutput.class);
            if(transactionOutput.getAddress().equals(fromAddress) && !transactionOutput.getSpent()){
                String[] dataOfOutput = key.split(":");
                TransactionInput transactionInput = new TransactionInput(dataOfOutput[0], Long.valueOf(dataOfOutput[1]), fromAddress, transactionOutput.getAmount());
                amountInUTXO = amountInUTXO.add(transactionOutput.getAmount());
                transactionInputs.add(transactionInput);
                if(amountInUTXO.compareTo(amount) >= 0){
                    return transactionInputs;
                }
            }

        }
        return new ArrayList<>();
    }
}
