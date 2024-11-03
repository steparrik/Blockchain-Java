package org.blockchain_java;

import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.Blockchain;
import org.blockchain_java.blockchain.models.transaction.Transaction;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.blockchain_java.blockchain.service.BlockService;
import org.blockchain_java.blockchain.service.BlockchainService;
import org.blockchain_java.blockchain.service.transaction.TransactionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class BlockchainJavaApplication {
	static Map<String, TransactionOutput> UTXO = new HashMap<>();

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		ApplicationContext context = SpringApplication.run(BlockchainJavaApplication.class, args);
		BlockchainService blockchainService = context.getBean(BlockchainService.class);
		Blockchain blockchain = context.getBean(Blockchain.class);
		TransactionService transactionService = context.getBean(TransactionService.class);
		BlockService blockService = context.getBean(BlockService.class);

		UTXO.put("stepa:0", new TransactionOutput("stepa", new BigDecimal(1000), false));

		while (true){
			String from = sc.next();
			String to = sc.next();
			String amount = sc.next();

			Transaction transaction = transactionService.createTransaction(from, to, new BigDecimal(amount), UTXO);
			if(transaction == null){
				System.out.println(UTXO);
				System.out.println("Недостаточно средств");
				continue;
			}

			Block blockPreMine = blockService.createBlock(blockchain.getChain(), Collections.singletonList(transaction), UTXO);
			Block blockAfterMine = blockchainService.mineBlock(blockPreMine, blockchain.getDifficultyPrefix());
			blockchain.getChain().add(blockAfterMine);

			System.out.println(blockchain.toString());
			System.out.println(UTXO);
		}

	}

}
