package org.steparrik.blockchain.tcp;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.util.List;

public class TcpClient {
    private List<String> nodesData;

    public TcpClient(List<String> nodesData) {
        this.nodesData = nodesData;
    }

    public void sendTransactionToAll(String transaction) {
        for (String nodeData : nodesData) {
            String[] nodeDataArr = nodeData.split(":");
            String nodeIp = nodeDataArr[0];
            Integer nodePort = Integer.valueOf(nodeDataArr[1]);
            new Thread(() -> sendMessage(nodeIp, nodePort, transaction)).start();
        }
    }


    public void sendMessageToAll(String message) {
        for (String nodeData : nodesData) {
            String[] nodeDataArr = nodeData.split(":");
            String nodeIp = nodeDataArr[0];
            Integer nodePort = Integer.valueOf(nodeDataArr[1]);
            new Thread(() -> sendMessage(nodeIp, nodePort, message)).start();
        }
    }

    private void sendMessage(String nodeIp, Integer nodePort, String message) {
        try (Socket socket = new Socket(nodeIp, nodePort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             ) {

            out.println(message);
            System.out.println("Sent message to " + nodeIp + ": " + message);

        } catch (IOException e) {
            System.out.println("Error connecting to " + nodeIp + ":"+ nodePort + " - " + e.getMessage());
        }
    }
}
