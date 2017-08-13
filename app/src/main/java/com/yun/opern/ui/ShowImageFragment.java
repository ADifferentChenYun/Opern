package com.yun.opern.ui;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.yun.opern.R;
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.utils.T;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImageFragment extends Fragment {


    @BindView(R.id.photo_view)
    PhotoView photoView;
    Unbinder unbinder;
    @BindView(R.id.loading_pb)
    ProgressBar loadingPb;
    private OpernImgInfo opernImgInfo;

    private int retry = 3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        opernImgInfo = (OpernImgInfo) getArguments().get("opernImgInfo");
        View view = inflater.inflate(R.layout.fragment_show_image, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        Glide.with(this).load(opernImgInfo.getOpernImg()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (retry > 0) {
                    initView();
                    T.showShort("加载图片失败，正在重新加载");
                } else {
                    T.showShort("加载图片失败，请重试");
                    getActivity().finish();
                }
                retry--;
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadingPb.setVisibility(View.GONE);
                return false;
            }
        }).into(photoView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
