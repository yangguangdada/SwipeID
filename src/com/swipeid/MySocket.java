package com.example.app_10;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MySocket {
    private static final String ip="192.168.56.1";
    private static final int port=9999;
    private static final int reg_data = 1;
    private static final int reg_request = 2;
    private static final int auth_data = 3;
    private static final int auth_request = 4;
    public static void Register(String path,Context context) throws IOException {
        if(Build.VERSION.SDK_INT>8){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //注册
        try{
            SendData(path,reg_data);
            if(Request(reg_request)) Toast.makeText(context,"注册成功！",Toast.LENGTH_SHORT).show();
            else Toast.makeText(context,"注册失败！",Toast.LENGTH_SHORT).show();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("Register:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("Register:" + e.getMessage()));}
    }
    public static void Authentic(String path,Context context) throws IOException {
        if(Build.VERSION.SDK_INT>8){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //认证
        try{
            SendData(path,auth_data);
            if(Request(auth_request)) Toast.makeText(context,"认证成功！",Toast.LENGTH_SHORT).show();
            else Toast.makeText(context,"认证失败！",Toast.LENGTH_SHORT).show();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("Authentic:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("Authentic:" + e.getMessage()));}
    }
    private static void SendData(String path,int func) throws UnknownHostException,IOException{
        File folder = new File(path);
        File[] files = folder.listFiles();//存储所有文件
        try{
            Socket socket=new Socket(ip,port);
            OutputStream out = socket.getOutputStream();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
            BufferedReader reader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (File file : files) {//遍历文件夹下文件并发送
                if (file.isFile()) {
                    writer.write(func);
                    String filename = file.getName();
                    writer.write(filename);
                    long filelength=file.length();
                    writer.write((int) filelength);
                    //写入认证文件数据
                    FileInputStream inputStream = new FileInputStream(file);
                    byte[] buff = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buff)) != -1) {
                        out.write(buff, 0, len);
                    }
                    inputStream.close();
                }
            }
            writer.flush();
            writer.close();
            socket.close();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("SendData:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("SendData:" + e.getMessage()));
        }
    }
    private static boolean Request(int func) throws UnknownHostException,IOException {
        final String username = "lxq";
        try{
        Socket socket=new Socket(ip,port);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //写入认证信息
        writer.write(func);
        writer.write(username);
        Random rand=new Random();
        int id = rand.nextInt(1000000);
        writer.write(String.valueOf(id));
        //读取服务器返回数据包
        int type = reader.read();
        int result = reader.read();
        int ackid = reader.read();
        if(type == 5 && result == 1 && ackid == id) return true;
        writer.flush();
        writer.close();
        socket.close();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("Request:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("Request:" + e.getMessage()));
        }
        return false;
    }
}
