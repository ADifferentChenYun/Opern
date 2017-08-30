package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.ui.fragments.ShowImageFragment;
import com.yun.opern.utils.FileUtil;
import com.yun.opern.utils.ImageDownloadUtil;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowImageActivity extends BaseActivity {

    @BindView(R.id.image_vp)
    ViewPager imageVp;
    @BindView(R.id.download_fab)
    FloatingActionButton downloadFab;
    @BindView(R.id.collection_fab)
    FloatingActionButton collectionFab;
    @BindView(R.id.fab_btns)
    LinearLayout fabBtns;
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;

    private OpernInfo opernInfo;
    private ViewPagerAdapter adapter;

    @Override
    protected int contentViewRes() {
        opernInfo = (OpernInfo) getIntent().getExtras().get("opernInfo");
        return R.layout.activity_show_image;
    }

    @Override
    protected void initView() {
        actionbar.setTitle(opernInfo.getTitle());
        ArrayList<ShowImageFragment> fragments = new ArrayList<>();
        for (OpernImgInfo opernImgInfo : opernInfo.getImgs()) {
            ShowImageFragment showImageFragment = new ShowImageFragment(actionbar, fabBtns);
            Bundle bundle = new Bundle();
            bundle.putSerializable("opernImgInfo", opernImgInfo);
            showImageFragment.setArguments(bundle);
            fragments.add(showImageFragment);
        }
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        imageVp.setAdapter(adapter);
        imageVp.setOffscreenPageLimit(fragments.size() - 1);
        imageVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        downloadFab.setOnClickListener((view) ->
                new ImageDownloadUtil(opernInfo, new ImageDownloadUtil.CallBack() {
                    @Override
                    public void success() {
                        downloadFab.setVisibility(View.GONE);
                        T.showShort("下载成功");
                    }

                    @Override
                    public void fail() {
                        T.showShort("下载失败，请重试");
                    }
                }).start()

        );
        collectionFab.setOnClickListener((view) -> {
                    Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
                    WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
                    if (!accessToken.isSessionValid() || weiBoUserInfo == null) {
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setTitle("收藏")
                                .setMessage("登录之后才能使用收藏功能哦~")
                                .setPositiveButton("登录", (dialog, which) -> startActivity(new Intent(context, MoreActivity.class)))
                                .setCancelable(true)
                                .create();
                        alertDialog.show();
                    } else {
                        addCollection();
                    }
                }
        );
        downloadFab.setVisibility(FileUtil.isOpernImgsExist(opernInfo) ? View.GONE : View.VISIBLE);
        isCollected();
    }

    /**
     * 是否收藏
     */
    public void isCollected() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (weiBoUserInfo == null) {
            return;
        }
        HttpCore.getInstance().getApi().isCollected(weiBoUserInfo.getId(), opernInfo.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().isSuccess()) {
                    T.showShort(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 收藏
     */
    public void addCollection() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi().addCollection(weiBoUserInfo.getId(), opernInfo.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().isSuccess()) {
                    T.showShort(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * 取消收藏
     */
    public void removeCollect() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi().removeCollection(weiBoUserInfo.getId(), opernInfo.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.body().isSuccess()) {
                    T.showShort(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<ShowImageFragment> fragments;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<ShowImageFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return opernInfo.getImgs().size();
        }
    }
}
