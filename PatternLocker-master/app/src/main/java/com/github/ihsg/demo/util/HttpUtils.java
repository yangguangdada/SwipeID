package com.github.ihsg.demo.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import okhttp3.*;

public class HttpUtils {

    public ResponseBody upload(Context ctx, int type) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String url = "10.12.151.2";
        String way, filename;

        File recordPath = new File(ctx.getFilesDir() + File.separator + "/audio");
        if (type == 0){
            filename = "train.amr";
        }else{
            filename = "verify.amr";
        }
        if (type == 0){
            way = "/train";
        }else{
            way = "/verify";
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(recordPath.getAbsolutePath())))
                .build();


        Request request = new Request.Builder()
                .url("http://" + url + way)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body();
    }
}
