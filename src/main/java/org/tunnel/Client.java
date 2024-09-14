package org.tunnel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.tunnel.config.SocketService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;

public class Client {

    static RestTemplate restTemplate = new RestTemplate();
    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 8070;
        try (SocketService socketService = new SocketService(hostName, portNumber)) {
             new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = socketService.receiveMessage()) != null) {
                        System.out.println("Recive Message: " + serverMessage);
                        String url = "http://localhost:1234"+serverMessage;
                        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
                        socketService.sendMessage(forEntity.getBody());
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            }).start();

            // Main thread to send messages to the server
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                if (userInputReader.readLine().equalsIgnoreCase("bye")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}