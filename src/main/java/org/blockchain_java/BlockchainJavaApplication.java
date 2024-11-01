package org.blockchain_java;

import org.blockchain_java.blockchain.models.Block;
import org.blockchain_java.blockchain.models.Blockchain;
import org.blockchain_java.blockchain.models.Transaction;
import org.blockchain_java.blockchain.service.BlockService;
import org.blockchain_java.blockchain.service.BlockchainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

@SpringBootApplication
public class BlockchainJavaApplication {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ApplicationContext context = SpringApplication.run(BlockchainJavaApplication.class, args);
		BlockchainService blockchainService = context.getBean(BlockchainService.class);
		Blockchain blockchain = context.getBean(Blockchain.class);
		BlockService blockService = context.getBean(BlockService.class);

		while (true){
			String from = sc.next();
			String to = sc.next();
			String amount = sc.next();

			Transaction transaction = new Transaction("some_hash", from, to, new BigDecimal(amount), new Date().getTime());
			Block blockPreMine = blockService.createBlock(blockchain.getChain(), Collections.singletonList(transaction));
			Block blockAfterMine = blockchainService.mineBlock(blockPreMine, blockchain.getDifficultyPrefix());
			blockchain.getChain().add(blockAfterMine);

			System.out.println(blockchain.toString());
		}

	}

}
