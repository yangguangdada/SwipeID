package com.swipeid;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

import com.swipeid.util.ConsoleErrorWriter;
import com.swipeid.util.ConsoleWriter;
import com.mathworks.engine.*;
import org.omg.CORBA.portable.OutputStream;

import static com.mathworks.mvm.MvmSession.SessionOptions.PrimaryMode.Matlab;

public class test {
    public static void  main(String[] args){
        Instant instant = Instant.now();
        long currentTimestamp = instant.toEpochMilli();
        System.out.println("当前时间戳：" + currentTimestamp);
//        try{
//            Add func = new Add();
//            int[] x = new int[3];
//            x[0]=0;
//            x[1]=1;
//            x[2]=2;
//            int[] y = new int[3];
//            y[0]=0;
//            y[1]=1;
//            y[2]=2;
//            //func.cal_add(x,y);
//            Object[] result = func.cal_add(1,x, y);
//            for(int i = 0; i<1; i++){
//                System.out.println(result[i]);
//            }
//            Object[] result1 = func.cal_add(1,x, y);
//            for(int i = 0; i<1; i++){
//                System.out.println(result1[i]);
//            }
//            Object[] result2 = func.cal_add(1,x, y);
//            for(int i = 0; i<1; i++){
//                System.out.println(result2[i]);
//            }
//            Object[] result3 = func.cal_add(1,x, y);
//            for(int i = 0; i<1; i++){
//                System.out.println(result3[i]);
//            }
//        } catch (MWException e){
//            e.printStackTrace();
//        }
        try{

            MatlabEngine matlabEngine = MatlabEngine.startMatlab();
            ConsoleWriter w = new ConsoleWriter();
            ConsoleErrorWriter e = new ConsoleErrorWriter();
            // 将 MATLAB 输出打印到当前控制台
            matlabEngine.eval("addpath(genpath('fs_auth'))");
            String username ="'lxq'";
            String train_path = "'data\\train'";
            String save_path = "'models\\'";
            String com = "register_user(" + username+ "," + train_path + "," +save_path + ")";
            //matlabEngine.eval("register_user(" + username+ "," + train_path + "," +save_path + ")",w,e);
            String val_path = "'data\\val'";
            com = "result = val_user(" + username + "," + val_path + "," + save_path + ")";
            matlabEngine.eval(com,w,e);
            double res= matlabEngine.getVariable("result");
            System.out.println(res);
            matlabEngine.close();

        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }
    }
}

