package com.swipeid;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务器启动，等待客户端连接"+"监听port:"+serverSocket.getLocalPort());
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("socket "+"port:" + socket.getLocalPort());
                System.out.println("客户端已连接");
                new Thread(new FileReceiver(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class FileReceiver implements Runnable {
        private Socket socket;

        public FileReceiver(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream inputStream = socket.getInputStream();
                 DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                String fileName = dataInputStream.readUTF();
                long fileSize = dataInputStream.readLong();

                try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)){
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long bytesLeft = fileSize;

                    while (bytesLeft > 0 && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, bytesLeft))) != -1) {
                        //fileOutputStream.write(buffer, 0, bytesRead);
                        bytesLeft -= bytesRead;
                    }

                    System.out.println("文件 " + fileName + " 接收成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}