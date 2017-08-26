package com.yun.opern.ui.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yun.opern.R;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.CacheFileUtil;

import io.reactivex.functions.Consumer;

public class LauncherActivity extends BaseActivity {

    @Override
    protected int contentViewRes() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //检测权限
                RxPermissions reRxPermissions = new RxPermissions((Activity) context);
                reRxPermissions
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {

                                       @Override
                                       public void accept(Boolean aBoolean) throws Exception {
                                           if (aBoolean) {
                                               //true表示获取权限成功（android6.0以下默认为true）
                                               //初始化缓存目录
                                               CacheFileUtil.init();
                                               startActivity(new Intent(context, MainActivity.class));
                                               finish();
                                           } else {
                                               System.exit(0);
                                           }
                                       }
                                   }
                        );
            }
        }, 1800);
    }


}
