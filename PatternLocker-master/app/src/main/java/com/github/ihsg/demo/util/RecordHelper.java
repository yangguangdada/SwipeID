package com.github.ihsg.demo.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class RecordHelper {
    private String filename, filePath;
    private MediaRecorder mRecorder;
    public void BeginRecord(Context ctx, int type){

        try{
            // audio file
            File recordPath = new File(ctx.getFilesDir() + File.separator + "/audio");
            if (!recordPath.mkdirs()){
                Log.println(Log.ERROR, "audio", "cannot create a dir or already have one");
            }

            // filename = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".amr";
            if (type == 0){
                filename = "train.amr";
            }else{
                filename = "verify.amr";
            }
            filePath = recordPath.getAbsolutePath() + "/" + filename;
            mRecorder = new MediaRecorder();
            //设置麦克风
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //输入文件格式
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //音频文件编码
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //输出文件路径
            mRecorder.setOutputFile(filePath);
        }catch (Exception e){
            e.printStackTrace();
        }

        //开始录音
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IllegalStateException e) {
           Log.println(Log.ERROR, "audio", "start");
        } catch (IOException e) {
            Log.println(Log.ERROR, "audio", "start");
            e.printStackTrace();
        }
    }

    public void StopRecord(){
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (IllegalStateException e) {
        }
    }

//    private boolean checkSDCard() {
//        // TODO Auto-generated method stub
//        //检测SD卡是否插入手机中
//        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            return true;
//        }
//        return false;
//    }

//    private boolean isExternalStorageWritable() {
//        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
//    }


}
