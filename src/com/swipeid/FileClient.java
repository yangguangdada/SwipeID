package com.swipeid;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {

        String[] filenames = new String[10];
        Scanner scanner = new Scanner(System.in);
        System.out.println("\"请提供需要传输的文件名\"");
        int i = 0;
        while(scanner.hasNextLine()){
            String filename = scanner.nextLine();
            if(filename.isEmpty()){
                break;
            }
            filenames[i++] = filename;
        }

        for (int j = 0;j < i; j++) {
            String fileName = filenames[j];
            Path filePath = Paths.get(fileName);
            if (!Files.exists(filePath)) {
                System.out.println("文件 " + fileName + " 不存在");
                continue;
            }
            try{
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                System.out.println("socket " + "port:" + socket.getLocalPort());
                new Thread(new FileSender(socket,fileName)).start();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
    private static class FileSender implements Runnable{
        private Socket socket;
        private String filename;
        public FileSender(Socket socket, String filename){
            this.socket = socket;
            this.filename = filename;
        }
        @Override
        public void run(){
            try(OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                FileInputStream fileInputStream = new FileInputStream(filename)){
                Path filepath = Paths.get(filename);
                dataOutputStream.writeUTF(filename);
                dataOutputStream.writeLong(Files.size(filepath)); //写入文件长度
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("文件 " + filename + " 传输成功");
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }

            }
        }
    }
}