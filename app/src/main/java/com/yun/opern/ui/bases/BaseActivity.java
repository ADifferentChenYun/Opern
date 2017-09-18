package com.yun.opern.ui.bases;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.yun.opern.views.ProgressDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Yun on 2017/8/25 0025.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Context context;
    protected ProgressDialog progressDialog;
    protected AlertDialog alertDialog;
    protected Unbinder unbinder;

    protected abstract int contentViewRes();

    protected abstract void initView();

    protected void initedView(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(contentViewRes());
        unbinder = ButterKnife.bind(this);
        initView();
        initedView();
    }

    protected void showProgressDialog(boolean show, DialogInterface.OnCancelListener onCancelListener) {
        if(show){
            if(progressDialog == null){
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                if(onCancelListener != null){
                    progressDialog.setOnCancelListener(onCancelListener);
                }
            }
            progressDialog.show();
        }else {
            if(progressDialog != null && progressDialog.isShowing()){
                new Handler().postDelayed(() -> progressDialog.dismiss(), 300);
            }
        }
    }

    protected void showProgressDialog(boolean show) {
        showProgressDialog(show, null);
    }

    protected void showDialog(String title, String message, String positive, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (positive != null && positiveListener != null) {
            builder.setPositiveButton(positive, positiveListener);
        }
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }

    protected void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
