package com.swipeid;
import java.io.*;
import java.net.*;

public class Server {
    private int portNumber;
    private String hostName;

    public Server(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void start() {
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            System.out.println("Client connected");

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received message: " + inputLine);
                out.println("Server received message: " + inputLine);
            }

            System.out.println("Client disconnected");
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.err.println(e.getMessage());
        }
    }
}