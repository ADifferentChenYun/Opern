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
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.ui.fragments.ShowImageFragment;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.FileUtil;
import com.yun.opern.utils.ImageDownloadUtil;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.ViewPagerFix;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class ShowImageActivity extends BaseActivity {

    @BindView(R.id.image_vp)
    ViewPagerFix imageVp;
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

    private boolean isCollected = false;
    private ImageDownloadUtil imageDownloadUtil;

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
            ShowImageFragment showImageFragment = new ShowImageFragment();
            showImageFragment.setAnimView(actionbar, fabBtns);
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
        downloadFab.setOnClickListener((view) -> {
                    imageDownloadUtil = new ImageDownloadUtil(opernInfo, new ImageDownloadUtil.CallBack() {
                        @Override
                        public void success() {
                            if (downloadFab != null) {
                                downloadFab.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void fail() {
                            ErrorMessageUtil.showErrorByToast("下载失败，请重试");
                        }
                    });
                    imageDownloadUtil.start();
                }
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
                        if (isCollected) {
                            removeCollect();
                        } else {
                            addCollection();
                        }

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
        HttpCore.getInstance().getApi().isCollected(weiBoUserInfo.getId(), opernInfo.getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    if (baseResponse.isSuccess()) {
                        isCollected = true;
                    } else {
                        isCollected = false;
                    }
                    changeCollectIcon();
                }, Throwable::printStackTrace);
    }

    /**
     * 收藏
     */
    public void addCollection() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi()
                .addCollection(weiBoUserInfo.getId(), opernInfo.getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                            if (baseResponse.isSuccess()) {
                                isCollected = true;
                                changeCollectIcon();
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                            ErrorMessageUtil.showErrorByToast(throwable);
                        }
                );
    }

    /**
     * 取消收藏
     */
    public void removeCollect() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi()
                .removeCollection(weiBoUserInfo.getId(), opernInfo.getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    if (baseResponse.isSuccess()) {
                        isCollected = false;
                        changeCollectIcon();
                        ErrorMessageUtil.showErrorByToast(baseResponse.getMessage());
                    }
                }, Throwable::printStackTrace);
    }

    public void changeCollectIcon() {
        collectionFab.setImageResource(isCollected ? R.mipmap.ic_collected : R.mipmap.ic_collection);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageDownloadUtil != null) {
            imageDownloadUtil.cancel();
        }
    }
}
