package com.swipeid;

import com.mathworks.engine.MatlabEngine;
import com.swipeid.util.ConsoleErrorWriter;
import com.swipeid.util.ConsoleWriter;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;   //异步模型
import java.util.concurrent.Semaphore;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private Future<MatlabEngine> engine;
    private static final int TYPE_1 = 1;
    private static final int TYPE_2 = 2;
    private static final int TYPE_3 = 3;
    private static final int TYPE_4 = 4;
    private static final int TYPE_5 = 5;
    private static final String[] BEHAVIOUR_TYPE = {"","click", "slide", "pinch", "handwriting"};
    private static final int[] NUM_REGISTER = {0, 25, 50, 25, 25};
    public static Semaphore valTransFinished = new Semaphore(1);
    public static Semaphore trainTransFinished = new Semaphore(1);


    private static final String MODELS_ROOT_PATH = "Models";
    private static final String TRAIN_ROOT_PATH = "data/train/";
    private static final String VAL_ROOT_PATH = "data/val";

    public ConnectionHandler(Socket socket, Future<MatlabEngine> engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @Override
    public void run(){
        try(InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);){   //使用字节输入流
            int type = dataInputStream.readInt();  //读取数据包类型
            switch (type){
                case TYPE_1:
                    System.out.println("客户端*注册*数据传输 已连接 port:"+socket.getRemoteSocketAddress());
                    socket.setSoTimeout(20000);  //设置超时时间为20秒
                    fileReceive(dataInputStream, TYPE_1);
                    break;
                case TYPE_2:
                    System.out.println("客户端*注册*控制传输 已连接 port:"+socket.getRemoteSocketAddress());
                    registerHandler(dataInputStream);
                    break;
                case TYPE_3:
                    System.out.println("客户端*认证*数据传输 已连接 port:"+socket.getRemoteSocketAddress());
                    socket.setSoTimeout(20000);  //设置超时时间为20秒
                    fileReceive(dataInputStream, TYPE_3);
                    break;
                case TYPE_4:
                    System.out.println("客户端*认证*控制传输 已连接 port:"+socket.getRemoteSocketAddress());
                    authenticateHandler(dataInputStream);
                    break;
                case TYPE_5:
                default:
                    throw new IllegalArgumentException("package type is unknown");

            }
            if(socket.isClosed()) System.out.println("客户端*注册*数据传输 已断开 port:"+socket.getRemoteSocketAddress());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void fileReceive(DataInputStream dataInputStream, int type){
        try{
            if(type ==TYPE_1 ) ConnectionHandler.trainTransFinished.acquire();
            else ConnectionHandler.valTransFinished.acquire();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        int a = 0;
        Instant instant = Instant.now();
        long currentTimestamp = instant.toEpochMilli();
        while(true){
            try{
                if(a == 1) {
                    //判断是否已经到达输入流尾部（必要，否则当客户端关闭socket连接时，read就会
                    //报io.EOFEXPECTION的错误）
                    //当客户端未关闭的socket时，并且输入流中无数据，那么read函数就会阻塞
                    //当客户端已关闭socket时，但是输入流中有数据，那么read函数仍然会正常返回
                    //dataInputStream.available返回当前时刻不会阻塞而可以读取的个数
                    if(dataInputStream.available() == 0) {
                        if(type==TYPE_1) ConnectionHandler.trainTransFinished.release();
                        else ConnectionHandler.valTransFinished.release();
                        break;
                    }
                    dataInputStream.readInt();
                }
                a = 1;
                //识别本次数据传输的类别 点击1 滑动2 捏合3 手写4
                int dataType = dataInputStream.readInt();
                String filename = dataInputStream.readUTF();
                String username = filename.split("_")[0];
                Path filepath;
//                //注册阶段
//                if (type==TYPE_1){
//                    filepath = Paths.get(TRAIN_ROOT_PATH, username, BEHAVIOUR_TYPE[dataType], filename);
//                }
//                //认证阶段
//                else{
//                    filepath = Paths.get(VAL_ROOT_PATH, username, BEHAVIOUR_TYPE[dataType], String.valueOf(currentTimestamp), filename);
//                }
                filepath = Paths.get((type == TYPE_1) ? TRAIN_ROOT_PATH : VAL_ROOT_PATH, username, BEHAVIOUR_TYPE[dataType], String.valueOf(currentTimestamp), filename);
                int fileSize = dataInputStream.readInt();
                Path dirPath = filepath.getParent();
                Files.createDirectories(dirPath);
                try(FileOutputStream outfile = new FileOutputStream(filepath.toString())){
                    byte[] buffer = new byte[4096];
                    int bytesRead = 0;
                    long bytesLeft = fileSize;
                    //当使用一条socket连接传输多个文件时，bytesLeft是必要的，可以保证read不会跨文件流
                    while(bytesLeft>0 && (bytesRead = dataInputStream.read(buffer,0 , (int)Math.min(buffer.length, bytesLeft))) !=-1){
                        outfile.write(buffer, 0, bytesRead);
                        bytesLeft -= bytesRead;
                    }
                    System.out.println(filename + "接收完成" +"to " + filepath.toString());

                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void registerHandler(DataInputStream dataInputStream){
        try{
            ConnectionHandler.trainTransFinished.acquire();
            ConnectionHandler.trainTransFinished.release();
            MatlabEngine eng = engine.get();  //获取matlab引擎
            int registerType = dataInputStream.readInt();  //获取注册类型
            String username = dataInputStream.readUTF();  //读取用户名
            int req_id = dataInputStream.readInt();       //读取ID号
            System.out.println("注册类型: " + BEHAVIOUR_TYPE[registerType]);
            String suffix0 = ".wav";
            String suffix1 = ".txt";
            //check train dataset
            Path trainDir=Paths.get(TRAIN_ROOT_PATH, username, BEHAVIOUR_TYPE[registerType]);
            String[] trainDirslist = trainDir.toFile().list();
            if (trainDirslist.length==0) throw  new NoSuchFileException(username + " no train dir");
            trainDir = Paths.get(trainDir.toString(), trainDirslist[trainDirslist.length-1]);
            if(Files.exists(trainDir) && Files.isDirectory(trainDir)){
                short wavCount = (short)Files.list(trainDir)
                        .filter(p -> p.toString().endsWith(suffix0))
                        .count();
                short txtCount = (short)Files.list(trainDir)
                        .filter(p -> p.toString().endsWith(suffix1))
                        .count();
                switch (registerType){
                    case 1:
                        if(txtCount!=1) throw new IllegalArgumentException("nums of trainfiles is not enough");
                        break;
                    case 2:
                        if(wavCount<NUM_REGISTER[2] || txtCount!=1) {
                            throw new IllegalArgumentException("just " + wavCount + " nums of trainfiles is not enough");
                        }

                        break;
                    case  3:
                        if(txtCount!=1) throw new IllegalArgumentException("nums of trainfiles is not enough");
                        break;
                    case 4:
                        if(wavCount<NUM_REGISTER[4] || txtCount!=1) throw new IllegalArgumentException("nums of trainfiles is not enough");
                        break;
                }
            }
            else throw new NoSuchFileException("not have trainfiles of "+ username);
            ConsoleWriter w = new ConsoleWriter();
            ConsoleErrorWriter e = new ConsoleErrorWriter();
            // 将 MATLAB 输出打印到当前控制台
            eng.eval("addpath(genpath('auth'))");   //genpath可以递归的搜索子目录
            String com = "result = register_user('" + username+ "','" + BEHAVIOUR_TYPE[registerType] +"','"+ TRAIN_ROOT_PATH + "','" + MODELS_ROOT_PATH + "')";
            System.out.println(com);
            eng.eval(com,w,e);
            double result = eng.getVariable("result");
            if(result==1)
                System.out.println(username + " 注册成功 ！！！！！！！！！！");
            else
                System.out.println(username + "注册失败  ！！！！！！！！！！");
            sendAck((int)result,req_id);

        } catch (ExecutionException | InterruptedException| IOException e){
            e.printStackTrace();
        }
    }

    private void authenticateHandler(DataInputStream dataInputStream){
        try {
            ConnectionHandler.valTransFinished.acquire();
            ConnectionHandler.valTransFinished.release();
            MatlabEngine eng = engine.get();
            int valType = dataInputStream.readInt();   //获取认证请求类型
            String username = dataInputStream.readUTF();
            int req_id = dataInputStream.readInt();
            System.out.println("认证类型: " + BEHAVIOUR_TYPE[valType]);
            ConsoleErrorWriter e = new ConsoleErrorWriter();
            ConsoleWriter w = new ConsoleWriter();
            //check models
            Path modelsPath= Paths.get(MODELS_ROOT_PATH,username,BEHAVIOUR_TYPE[valType]);
            File modelsDir = new File(modelsPath.toString());
            if(modelsDir.exists()&&modelsDir.isDirectory()){
                //不同类型的认证请求执行不同类型的检查
                switch (valType){
                    case 1:
                        break;
                    case 2:
                        int modelCount = modelsDir.list().length;
                        if(modelCount!=2) {
                            System.out.println(modelCount);
                            throw new NoSuchFileException("lack model file");
                        }
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
            else throw new IllegalArgumentException(username +"is not registered");
            //check val data
            Path valPath = Paths.get(VAL_ROOT_PATH,username,BEHAVIOUR_TYPE[valType]);
            File valDir = new File(valPath.toString());
            if(valDir.exists()&& valDir.isDirectory()){
                File[] valFiles = valDir.listFiles();
                if(valFiles.length==0) throw new NoSuchFileException("need val data");
            }
            else throw new NoSuchFileException("need val data");
            eng.eval("addpath(genpath('auth'))");   //genpath可以递归的搜索子目录
            String com = "result = val_user('"+username+"','" + BEHAVIOUR_TYPE[valType] + "','" + VAL_ROOT_PATH + "','"+MODELS_ROOT_PATH+"')";
            System.out.println(com);
            eng.eval(com,w,e);
            Double result = eng.getVariable("result");
            if(result==1)
                System.out.println( " 合法用户 ！！！！！！！！！！");
            else
                System.out.println("  非法用户 ！！！！！！！！！！");
            sendAck(result.intValue(),req_id);
        }catch (IOException | InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }

    private void sendAck(int result, int id){
        try(OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream)){
            dataOutputStream.writeInt(TYPE_5);
            dataOutputStream.writeInt(result);
            dataOutputStream.writeInt(id);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
