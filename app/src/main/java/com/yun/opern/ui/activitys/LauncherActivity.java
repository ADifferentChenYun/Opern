package com.yun.opern.ui.activitys;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.widget.LinearLayout;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yun.opern.R;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.NetworkUtils;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;

import static com.yun.opern.utils.NetworkUtils.NetworkType.NETWORK_WIFI;

public class LauncherActivity extends BaseActivity {
    @BindView(R.id.bottom_bar)
    LinearLayout bottomBar;

    private Disposable disposable;
    private Handler handler;
    private Runnable checkPermissionRunnable;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_launcher;
    }

    @Override
    protected void initView() {
        if (NetworkUtils.getNetworkType() != NETWORK_WIFI) {
            ErrorMessageUtil.showErrorByToast("当前处于非WIFI环境");
        }
        handler = new Handler();
        checkPermissionRunnable = () -> {
            //检测权限
            RxPermissions reRxPermissions = new RxPermissions(LauncherActivity.this);
            disposable = reRxPermissions
                    .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
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
                    );
        };
        handler.postDelayed(checkPermissionRunnable, 1800);
        bottomBar.animate().alpha(1f).setDuration(1500).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(checkPermissionRunnable);
        }
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
