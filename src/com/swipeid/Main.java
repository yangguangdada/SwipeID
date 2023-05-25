package com.swipeid;

public class Main {
    public static void main(String[] args) {
        String hostName= "localhost";
        int portNumber = 1234;
        Server server = new Server(hostName, portNumber);
        server.start();
    }
}