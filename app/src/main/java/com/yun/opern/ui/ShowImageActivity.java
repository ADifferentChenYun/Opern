package com.yun.opern.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yun.opern.R;
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.utils.FileUtil;
import com.yun.opern.utils.ImageDownloadUtil;
import com.yun.opern.utils.T;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowImageActivity extends AppCompatActivity {

    @BindView(R.id.image_vp)
    ViewPager imageVp;
    @BindView(R.id.download_fab)
    FloatingActionButton downloadFab;
    @BindView(R.id.collection_fab)
    FloatingActionButton collectionFab;

    private OpernInfo opernInfo;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        opernInfo = (OpernInfo) getIntent().getExtras().get("opernInfo");
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ArrayList<ShowImageFragment> fragments = new ArrayList<>();
        for (OpernImgInfo opernImgInfo : opernInfo.getImgs()) {
            ShowImageFragment showImageFragment = new ShowImageFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("opernImgInfo", opernImgInfo);
            showImageFragment.setArguments(bundle);
            fragments.add(showImageFragment);
        }
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        imageVp.setAdapter(adapter);
        imageVp.setOffscreenPageLimit(fragments.size() - 1);
        downloadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                }).start();
            }
        });
        collectionFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        downloadFab.setVisibility(FileUtil.isOpernImgsExist(opernInfo) ? View.GONE : View.VISIBLE);
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
