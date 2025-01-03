package org.steparrik.blockchain.tcp;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.steparrik.blockchain.models.Block;
import org.steparrik.blockchain.models.transaction.Transaction;
import org.steparrik.blockchain.service.block.BlockService;
import org.steparrik.blockchain.service.mempool.MempoolService;
import org.steparrik.blockchain.service.utxo.UtxoService;
import org.steparrik.blockchain.utils.exception.ValidateTransactionException;

import javax.sql.rowset.spi.TransactionalWriter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TcpServer {
    @Value("${tcp.port}")
    private Integer TCP_PORT;
    private final Gson gson;
    private final MempoolService mempoolService;
    private final List<String> nodesData = List.of("blockchain1:9000", "blockchain2:9000", "blockchain3:9000", "blockchain4:9000", "blockchain5:9000");
    private final BlockService blockService;
    private final UtxoService utxoService;

    @PostConstruct
    public void startTcpServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                System.out.println("TCP Server started on port " + TCP_PORT);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New connection from " + clientSocket.getInetAddress());

                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                System.out.println("Received: " + receivedMessage);
                TcpClient tcpClient = new TcpClient(nodesData);

                if (receivedMessage.contains("previousHash")) {
                    Block block = gson.fromJson(receivedMessage, Block.class);

                    String previousBlockHash = gson.fromJson(blockService.get("last"), String.class);

                    if(block.getPreviousHash().equals(previousBlockHash) || previousBlockHash == null){
                        if(newBlockProcessing(block)){
                            for(Transaction transaction : block.getTransactions()){
                                utxoService.removeSpentOutput(transaction);
                                utxoService.addNewOutput(transaction);
                            }
                            blockService.addNewBlock(block);
                            mempoolService.removeTransactions(block.getTransactions().stream().map(Transaction::getHash).toList());
                            tcpClient.sendMessageToAll(receivedMessage);
                        }
                    }
                } else {
                    Transaction transaction = gson.fromJson(receivedMessage, Transaction.class);
                    if (!mempoolService.getMempool().containsKey(transaction.getHash())) {
                        mempoolService.addTransaction(transaction);

                        tcpClient.sendMessageToAll(receivedMessage);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean newBlockProcessing(Block block) {
        String blockHash = block.getHash();
        String previousBlockHash = gson.fromJson(blockService.get("last"), String.class);
        if(blockHash.equals(blockService.calculateBlockHash(block)) && block.getPreviousHash().equals(previousBlockHash)){
            for(Transaction transaction : block.getTransactions()){
                try{
                    mempoolService.validateTransaction(transaction.getInputs());
                }catch (ValidateTransactionException e){
                    System.out.println(e);
                    return false;
                }
            }
        }
        return true;
    }
}
