package com.swipeid;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.mathworks.engine.MatlabEngine;
import com.swipeid.ConnectionHandler;

public class SwipeIDServer {
    private static final int PORT = 8080;
    public static void main(String[] args){
        Future<MatlabEngine> matlabEngineFuture = MatlabEngine.startMatlabAsync();  //start matlab session async
        try(ServerSocket serverSocket = new ServerSocket(PORT)){   //开启服务器监听端口8080
            System.out.println("SwipeID has started on port:" + PORT );
            System.out.println("Waiting for connection ......");
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(new ConnectionHandler(socket, matlabEngineFuture)).start();

            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                matlabEngineFuture.get().close();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
