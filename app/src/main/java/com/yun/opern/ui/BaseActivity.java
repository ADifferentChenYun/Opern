package com.yun.opern.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yun.opern.views.ProgressDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Yun on 2017/8/25 0025.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Context context;
    private ProgressDialog progressDialog;
    private Unbinder unbinder;

    abstract int contentViewRes();

    protected void initView(){

    }

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

    public void showProgressDialog(boolean show, DialogInterface.OnCancelListener onCancelListener){
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
                progressDialog.dismiss();
            }
        }
    }

    public void showProgressDialog(boolean show){
        showProgressDialog(show, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
