package com.yun.opern.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.yun.opern.Application;
import com.yun.opern.BuildConfig;
import com.yun.opern.net.HttpCore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Request;
import okhttp3.Response;
import retrofit2.http.HTTP;

import static android.app.ProgressDialog.STYLE_HORIZONTAL;

/**
 * Created by Yun on 2017/9/11 0011.
 */

public class UpdateAsync extends AsyncTask<String, Integer, File> {
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    private ProgressDialog progressDialog;
    private Context context;

    public UpdateAsync(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(STYLE_HORIZONTAL);
        progressDialog.setTitle("下载");
        progressDialog.setMax(100);
        progressDialog.show();
    }

    @Override
    protected File doInBackground(String... strings) {
        String url = strings[0];
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            Response response = HttpCore.getInstance().getOkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                File file = new File(rootPath + "/opern.apk");
                if (file.exists()) {
                    file.delete();
                    file.createNewFile();
                } else {
                    file.createNewFile();
                }
                InputStream inputStream = response.body().byteStream();
                long length = response.body().contentLength();
                byte[] bs = new byte[1024];
                int len;
                OutputStream os = new FileOutputStream(file);
                double i = 0;
                while ((len = inputStream.read(bs)) != -1) {
                    os.write(bs, 0, len);
                    i = i + 1024;
                    publishProgress((int) Math.min((int) (i / length * 100), 100));
                }
                os.flush();
                os.close();
                inputStream.close();
                this.publishProgress(100);
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        progressDialog.dismiss();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri,
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }


}