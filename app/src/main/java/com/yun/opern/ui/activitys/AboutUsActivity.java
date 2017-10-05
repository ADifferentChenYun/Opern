package com.yun.opern.ui.activitys;

import android.support.v7.app.AlertDialog;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.bugly.beta.Beta;
import com.yun.opern.BuildConfig;
import com.yun.opern.R;
import com.yun.opern.model.UpdateInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.SPUtil;
import com.yun.opern.utils.T;
import com.yun.opern.utils.UpdateAsync;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.check_update_btn)
    Button checkUpdateBtn;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void initView() {
        checkUpdateBtn.setText(String.valueOf("当前版本 " + BuildConfig.VERSION_NAME + " 检测更新"));
        RxView.clicks(checkUpdateBtn).subscribe(o -> {
            checkVersion();
            //Beta.checkUpgrade();
        });
    }

    private void checkVersion() {
        HttpCore.getInstance().getApi().checkVersion()
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateInfoBaseResponse -> {
                    UpdateInfo updateInfo = updateInfoBaseResponse.getData();
                    if (updateInfo.getVersionCode() > BuildConfig.VERSION_CODE) {
                        //存在更新
                        StringBuilder sb = new StringBuilder();
                        sb.append("当前版本：");
                        sb.append(BuildConfig.VERSION_NAME);
                        sb.append("\n");
                        sb.append("最新版本：");
                        sb.append(updateInfo.getVersionName());
                        sb.append("\n");
                        sb.append("更新类型：");
                        sb.append(updateInfo.getUpdateType().equals("0") ? "推荐更新" : "强制更新");
                        sb.append("\n");
                        sb.append("更新时间：");
                        sb.append(updateInfo.getUpdateDataTime());
                        sb.append("\n");
                        sb.append("更新信息：");
                        sb.append(updateInfo.getUpdateMessage());
                        sb.append("\n");
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("更新")
                                .setMessage(sb.toString())
                                .setCancelable(false)
                                .setPositiveButton("更新", (dialog, which) -> {
                                    UpdateAsync updateAsync = new UpdateAsync(context, updateInfo.getUpdateType().equals("0"));
                                    String url = "";
                                    if (updateInfo.getDownloadOSSUrl() != null && !updateInfo.getDownloadOSSUrl().equals("")) {
                                        if (updateInfo.getDownloadOSSUrl().startsWith("http")) {
                                            url = updateInfo.getDownloadOSSUrl();
                                        } else {
                                            url = HttpCore.baseUrl + updateInfo.getDownloadOSSUrl();
                                        }
                                    } else if (updateInfo.getDownloadUrl() != null && !updateInfo.getDownloadUrl().equals("")) {
                                        if (updateInfo.getDownloadUrl().startsWith("http")) {
                                            url = updateInfo.getDownloadUrl();
                                        } else {
                                            url = HttpCore.baseUrl + updateInfo.getDownloadUrl();
                                        }
                                    }
                                    updateAsync.execute(url);
                                });
                        if (updateInfo.getUpdateType().equals("0")) {
                            builder.setCancelable(true);
                            builder.setNegativeButton("下次更新", null);
                            builder.setNeutralButton("不再提醒", (dialog, which) -> SPUtil.putInt(SPUtil.Update_No_Longer_Reminded_Key, updateInfo.getVersionCode()));
                        }
                        builder.create().show();
                    } else {
                        T.showShort("已经是最新版本了");
                    }
                }, t -> {
                    t.printStackTrace();
                    T.showShort(t.getMessage());
                });
    }

}
