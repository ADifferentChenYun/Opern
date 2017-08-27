package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.opern.R;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.views.ActionBarNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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


    @Override
    protected int contentViewRes() {
        return R.layout.activity_more;
    }

    @Override
    protected void initView() {
        appCacheSizeTv.setText(appCacheSizeTv.getText().toString() + CacheFileUtil.size());
    }

    @OnClick(R.id.my_download_rl)
    public void onMyDownloadRlClicked() {
        startActivity(new Intent(MoreActivity.this, MyDownloadActivity.class));
    }

    @OnClick(R.id.my_collection_rl)
    public void onMyCollectionRlClicked() {
    }

    @OnClick(R.id.tell_us_rl)
    public void onTellUsRlClicked() {
    }

    @OnClick(R.id.about_us_rl)
    public void onAboutUsRlClicked() {
    }

}
