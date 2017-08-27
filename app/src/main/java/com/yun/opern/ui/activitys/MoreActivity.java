package com.yun.opern.ui.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.yun.opern.Application;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoConstants;
import com.yun.opern.model.event.OpernFileDeleteEvent;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;

public class MoreActivity extends BaseActivity {

    @BindView(R.id.my_download_rl)
    RelativeLayout myDownloadRl;
    @BindView(R.id.my_collection_rl)
    RelativeLayout myCollectionRl;
    @BindView(R.id.tell_us_rl)
    RelativeLayout tellUsRl;
    @BindView(R.id.about_us_rl)
    RelativeLayout aboutUsRl;
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.user_head_img)
    CircleImageView userHeadImg;
    @BindView(R.id.user_name_tv)
    TextView userNameTv;
    @BindView(R.id.user_info_tv)
    TextView userInfoTv;
    @BindView(R.id.app_cache_size_tv)
    TextView appCacheSizeTv;
    @BindView(R.id.clear_app_cache_rl)
    RelativeLayout clearAppCacheRl;
    @BindView(R.id.push_switch)
    Switch pushSwitch;

    private SsoHandler mSsoHandler;

    @Override
    protected int contentViewRes() {
        EventBus.getDefault().register(this);
        return R.layout.activity_more;
    }

    @Override
    protected void initView() {
        AuthInfo mAuthInfo = new AuthInfo(this, WeiBoConstants.APP_KEY, WeiBoConstants.REDIRECT_URL, WeiBoConstants.SCOPE);
        WbSdk.install(this, mAuthInfo);
        appCacheSizeTv.setText("APP缓存:" + CacheFileUtil.size());
        pushSwitch.setChecked(!JPushInterface.isPushStopped(Application.getAppContext()));
    }

    @OnClick(R.id.user_info_rl)
    public void onUserInfoRlClicked() {
        mSsoHandler = new SsoHandler(MoreActivity.this);
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    @OnClick(R.id.my_download_rl)
    public void onMyDownloadRlClicked() {
        startActivity(new Intent(MoreActivity.this, MyDownloadActivity.class));
    }

    @OnClick(R.id.my_collection_rl)
    public void onMyCollectionRlClicked() {
    }

    @OnClick(R.id.clear_app_cache_rl)
    public void onClearAppCacheClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean clear = CacheFileUtil.clear();
                        T.showShort(clear ? "缓存已清除" : "清除缓存失败");
                        initView();
                    }
                })
                .setTitle("清除缓存")
                .setMessage("清除缓存会删除所有本地的曲谱哦~")
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    @OnClick(R.id.tell_us_rl)
    public void onTellUsRlClicked() {
    }

    @OnClick(R.id.about_us_rl)
    public void onAboutUsRlClicked() {
        startActivity(new Intent(context, AboutUsActivity.class));
    }

    @OnCheckedChanged(R.id.push_switch)
    public void onPushSwitchCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (JPushInterface.isPushStopped(Application.getAppContext())) {
                JPushInterface.resumePush(Application.getAppContext());
                T.showShort("推送已打开");
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("关闭推送")
                    .setMessage("关闭推送后就不会收到任何推送了哦~不过我不建议你这么做QAQ")
                    .setPositiveButton("关闭推送", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JPushInterface.stopPush(Application.getAppContext());
                            buttonView.setChecked(!JPushInterface.isPushStopped(Application.getAppContext()));
                        }
                    })
                    .setCancelable(true)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            buttonView.setChecked(!JPushInterface.isPushStopped(Application.getAppContext()));
                        }
                    })
                    .create();
            alertDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnOpernFileDeleted(OpernFileDeleteEvent opernFileDeleteEvent) {
        initView();
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            MoreActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Oauth2AccessToken mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        // 显示 Token
                        //updateTokenView(false);
                        // 保存 Token 到 SharedPreferences
                        AccessTokenKeeper.writeAccessToken(context, mAccessToken);
                        Toast.makeText(context, mAccessToken.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void cancel() {
            Toast.makeText(context, "取消", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(context, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
