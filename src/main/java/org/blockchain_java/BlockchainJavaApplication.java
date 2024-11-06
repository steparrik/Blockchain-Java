package org.blockchain_java;

import com.google.gson.Gson;
import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.Blockchain;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.block.BlockService;
import org.blockchain_java.blockchain.service.transaction.TransactionService;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.iq80.leveldb.DBIterator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class BlockchainJavaApplication {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		ApplicationContext context = SpringApplication.run(BlockchainJavaApplication.class, args);
		BlockService blockService = context.getBean(BlockService.class);
		Blockchain blockchain = context.getBean(Blockchain.class);
		TransactionService transactionService = context.getBean(TransactionService.class);
		UtxoService utxoService = context.getBean(UtxoService.class);
		Gson gson = context.getBean(Gson.class);

		utxoService.put("stepa:0", gson.toJson(new TransactionOutput("stepa", new BigDecimal(1000), false)));
		while (true){
			String from = sc.next();
			String to = sc.next();
			String amount = sc.next();

			Transaction transaction = transactionService.createTransaction(from, to, new BigDecimal(amount));
			if(transaction == null){
				System.out.println("Недостаточно средств");
				continue;
			}

			blockService.generateBlock(Collections.singletonList(transaction));

			DBIterator iterator = utxoService.iterator();
			iterator.seekToFirst();

			while (iterator.hasNext()){
				Map.Entry<byte[], byte[]> entry = iterator.next();
				String key = new String(entry.getKey());
				TransactionOutput transactionOutput = gson.fromJson(new String(entry.getValue()), TransactionOutput.class);
				System.out.println(transactionOutput);
			}

			DBIterator iterator1 = blockService.iterator();
			iterator1.seekToFirst();

			while (iterator1.hasNext()){
				Map.Entry<byte[], byte[]> entry = iterator1.next();
				String key = new String(entry.getKey());
				if(key.equals("last")){
					System.out.println(new String(entry.getValue()));
				}else {
					Block block = gson.fromJson(new String(entry.getValue()), Block.class);
					System.out.println(block);
				}
			}
		}

	}

}
