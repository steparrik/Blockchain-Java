package org.blockchain_java;

import com.google.gson.Gson;
import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.block.BlockService;
import org.blockchain_java.blockchain.service.utxo.UtxoService;
import org.blockchain_java.client.service.transaction.TransactionService;
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

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);

		ApplicationContext context = SpringApplication.run(BlockchainJavaApplication.class, args);
		BlockService blockService = context.getBean(BlockService.class);
		TransactionService transactionService = context.getBean(TransactionService.class);
		UtxoService utxoService = context.getBean(UtxoService.class);
		Gson gson = context.getBean(Gson.class);

		utxoService.put("hash:0", gson.toJson(new TransactionOutput("9f8af2b16e2dfb7e2ebc01545c4510443b95619d", new BigDecimal(1000), false, null)));
		while (true){
			String from = sc.next();
			String to = sc.next();
			String amount = sc.next();
			String pubKey = sc.next();
			String privateKey = sc.next();

			Transaction transaction = transactionService.createTransaction(from, to, new BigDecimal(amount),
					pubKey, privateKey);

//			if(transaction == null){
//				System.out.println("Недостаточно средств");
//				continue;
//			}

			System.out.println(transaction);

//			blockService.generateBlock(Collections.singletonList(transaction));
//
//			DBIterator iterator = utxoService.iterator();
//			iterator.seekToFirst();
//
//			while (iterator.hasNext()){
//				Map.Entry<byte[], byte[]> entry = iterator.next();
//				String key = new String(entry.getKey());
//				TransactionOutput transactionOutput = gson.fromJson(new String(entry.getValue()), TransactionOutput.class);
//				System.out.println(transactionOutput);
//			}
//
//			DBIterator iterator1 = blockService.iterator();
//			iterator1.seekToFirst();
//
//			while (iterator1.hasNext()){
//				Map.Entry<byte[], byte[]> entry = iterator1.next();
//				String key = new String(entry.getKey());
//				if(key.equals("last")){
//					System.out.println(new String(entry.getValue()));
//				}else {
//					Block block = gson.fromJson(new String(entry.getValue()), Block.class);
//					System.out.println(block);
//				}
//			}
		}

	}

}
