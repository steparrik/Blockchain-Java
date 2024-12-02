package org.steparrik.client;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.steparrik.client.client.BlockchainApi;
import org.steparrik.client.models.Wallet;
import org.steparrik.client.models.transaction.Transaction;
import org.steparrik.client.service.transaction.TransactionService;
import org.steparrik.client.service.wallet.WalletService;
import org.steparrik.client.utils.CredentialsService;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.Scanner;

@SpringBootApplication
@EnableFeignClients
public class ClientApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        BlockchainApi blockchainApi = context.getBean(BlockchainApi.class);
        WalletService walletService = context.getBean(WalletService.class);
        CredentialsService credentialsService = context.getBean(CredentialsService.class);
        TransactionService transactionService = context.getBean(TransactionService.class);
        Gson gson = context.getBean(Gson.class);


        while (true) {
            String command = sc.next();

            switch (command) {
                case "help":
                    System.out.println("ncred: Create new wallet\n" +
                            "pcred: Look a current wallet\n" +
                            "send: Send a transaction from your wallet\n" +
                            "csend: Send a transaction from custom wallet\n" +
                            "help: Look a commands list");
                    break;
                case "ncred":
                    KeyPair keyPair = credentialsService.generateKeyPair();
                    String newPubKey = credentialsService.keyToBase64(keyPair.getPublic());
                    String newPrivateKey = credentialsService.keyToBase64(keyPair.getPrivate());
                    String newAddress = credentialsService.publicKeyToAddress(newPubKey);
                    Wallet wallet = new Wallet(newAddress, newPubKey, newPrivateKey);
                    walletService.put("myWallet", gson.toJson(wallet));

                    System.out.println("Generated address: " + newAddress);
                    System.out.println("Public Key: " + newPubKey);
                    System.out.println("Private Key: " + newPrivateKey);
                    break;
                case "pcred":
                    Wallet currentWallet = gson.fromJson(walletService.get("myWallet"), Wallet.class);
                    System.out.println("Generated address: " + currentWallet.getAddress());
                    System.out.println("Public Key: " + currentWallet.getPublicKey());
                    System.out.println("Private Key: " + currentWallet.getPrivateKey());
                    break;
                case "send":
                    Wallet senderWallet = gson.fromJson(walletService.get("myWallet"), Wallet.class);
                    System.out.println("Enter recipient's address:");
                    String to = sc.next();
                    System.out.println("Enter amount:");
                    String amount = sc.next();

                    Transaction transaction = transactionService.createTransaction(senderWallet.getAddress(), to, new BigDecimal(amount),
                            senderWallet.getPublicKey(), senderWallet.getPrivateKey());

                    if (transaction == null) {
                        continue;
                    }

                    blockchainApi.sendTransaction(transaction);

                    System.out.println(blockchainApi.getAllBlockchain());
                    break;
                case "csend":
                    System.out.println("Enter sender's address:");
                    String fromAddress = sc.next();
                    System.out.println("Enter recipient's address:");
                    String toAddress = sc.next();
                    System.out.println("Enter amount:");
                    String amountTokens = sc.next();
                    System.out.println("Enter recipient's public key:");
                    String publicKey = sc.next();
                    System.out.println("WARNING: the private key will be used to sign the transaction (it will not go to the blockchain)\n" +
                            "Enter recipient's private key:");
                    String prKey = sc.next();


                    Transaction customTransaction = transactionService.createTransaction(fromAddress, toAddress, new BigDecimal(amountTokens),
                            publicKey, prKey);

                    if (customTransaction == null) {
                        continue;
                    }

                    try {
                        blockchainApi.sendTransaction(customTransaction);
                    } catch (Exception e) {
                        continue;
                    }

                    System.out.println(blockchainApi.getAllBlockchain());
                    break;
            }
        }
    }

}
