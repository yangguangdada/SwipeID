package com.example.app_10;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MySocket {
    private static final String ip="192.168.56.1";
    private static final int port=8080;
    private static final int reg_data = 1;
    private static final int reg_request = 2;
    private static final int auth_data = 3;
    private static final int auth_request = 4;
    private static final int id_range = 1000000;
    public static void Register(String path,Context context,int type) throws IOException {
        if(Build.VERSION.SDK_INT>8){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //注册
        try{
            SendData(path,reg_data,type);
            if(Request(reg_request,type)) Toast.makeText(context,"注册成功！",Toast.LENGTH_SHORT).show();
            else Toast.makeText(context,"注册失败！",Toast.LENGTH_SHORT).show();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("Register:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("Register:" + e.getMessage()));}
    }
    public static void Authentic(String path,Context context,int type) throws IOException {
        if(Build.VERSION.SDK_INT>8){
            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        //认证
        try{
            SendData(path,auth_data,type);
            if(Request(auth_request,type)) Toast.makeText(context,"认证成功！",Toast.LENGTH_SHORT).show();
            else Toast.makeText(context,"认证失败！",Toast.LENGTH_SHORT).show();
        }catch (UnknownHostException e){
            e.printStackTrace();
            Log.e(TAG, ("Authentic:" + e.getMessage()));
        }catch (IOException e){
            e.printStackTrace();
            Log.e(TAG, ("Authentic:" + e.getMessage()));}
    }
    private static void SendData(String path,int func,int type) throws UnknownHostException,IOException{
        File folder = new File(path);
        File[] files = folder.listFiles();//用数组存储所有文件
        try{
            Socket socket=new Socket(ip,port);
            OutputStream out = socket.getOutputStream();
            DataOutputStream writer=new DataOutputStream(out);
            DataInputStream reader =new DataInputStream(socket.getInputStream());
            for (File file : files) {//遍历文件夹下文件并发送
                if (file.isFile()) {
                    //写入认证文件信息
                    writer.writeInt(func);
                    writer.writeInt(type);
                    String filename = file.getName();
                    writer.writeUTF(filename);
                    long filelength=file.length();
                    writer.writeInt((int) filelength);
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
            //关闭连接
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
    private static boolean Request(int func,int type) throws UnknownHostException,IOException {
        final String username = "lxq";
        try{
            Socket socket=new Socket(ip,port);
            DataOutputStream writer =new DataOutputStream(socket.getOutputStream());
            DataInputStream reader =new DataInputStream(socket.getInputStream());
            //写入认证信息
            writer.writeInt(func);
            writer.writeInt(type);
            writer.writeUTF(username);
            Random rand=new Random();
            int id = rand.nextInt(id_range);
            writer.writeInt(id);
            //读取服务器返回数据包
            int Type = reader.read();
            int result = reader.read();
            int ackid = reader.read();
            //关闭连接
            writer.flush();
            writer.close();
            socket.close();
            if(Type == 5 && result == 1 && ackid == id) return true;
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
