package org.steparrik.client;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.steparrik.client.client.BlockchainApi;
import org.steparrik.client.models.transaction.Transaction;
import org.steparrik.client.service.transaction.TransactionService;
import org.steparrik.client.utils.CredentialsService;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.Scanner;

@SpringBootApplication
@EnableFeignClients
public class ClientApplication {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String pubKey = null, privateKey = null, address = null;

        ApplicationContext context = SpringApplication.run(ClientApplication.class, args);
        BlockchainApi blockchainApi = context.getBean(BlockchainApi.class);

        CredentialsService credentialsService = context.getBean(CredentialsService.class);
        TransactionService transactionService = context.getBean(TransactionService.class);


        while (true){
            String command = sc.next();

            if(command.equals("ncred")){
                KeyPair keyPair = credentialsService.generateKeyPair();
                pubKey = credentialsService.keyToBase64(keyPair.getPublic());
                privateKey = credentialsService.keyToBase64(keyPair.getPrivate());
                address = credentialsService.publicKeyToAddress(pubKey);
                blockchainApi.getAllBlockchain(address);
                System.out.println("Generated address: " + address);
                System.out.println("Public Key: " + pubKey);
                System.out.println("Private Key: " + privateKey);

            }else if(command.equals("pcred")){
                System.out.println("Generated address: " + address);
                System.out.println("Public Key: " + pubKey);
                System.out.println("Private Key: " + privateKey);
            } else if (command.equals("send")) {
                String to = sc.next();
                String amount = sc.next();

                Transaction transaction = transactionService.createTransaction(address, to, new BigDecimal(amount),
                        pubKey, privateKey);

                if(transaction == null){
                    continue;
                }

                blockchainApi.sendTransaction(transaction);

                System.out.println(blockchainApi.getAllBlockchain("test"));
            } else if (command.equals("csend")) {
                String from = sc.next();
                String to = sc.next();
                String amount = sc.next();
                String publicKey = sc.next();
                String prKey = sc.next();
                blockchainApi.getAllBlockchain(from);


                Transaction transaction = transactionService.createTransaction(from, to, new BigDecimal(amount),
                        publicKey, prKey);

                if(transaction == null){
                    continue;
                }

                try {
                    blockchainApi.sendTransaction(transaction);
                }catch (Exception e){
                    continue;
                }

                System.out.println(blockchainApi.getAllBlockchain("test"));
            }
    }
    }

}
