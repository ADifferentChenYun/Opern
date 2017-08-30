package com.yun.opern.ui.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.yun.opern.views.ActionBarNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowImageFragment extends Fragment {


    @BindView(R.id.photo_view)
    PhotoView photoView;
    Unbinder unbinder;
    @BindView(R.id.loading_pb)
    ProgressBar loadingPb;
    private ActionBarNormal actionBarNormal;
    private LinearLayout fabBtns;
    private OpernImgInfo opernImgInfo;

    private int retry = 3;

    public ShowImageFragment(ActionBarNormal actionBarNormal, LinearLayout fabBtns) {
        this.actionBarNormal = actionBarNormal;
        this.fabBtns = fabBtns;
    }

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
        Glide.with(this).asBitmap().load(opernImgInfo.getOpernImg()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
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
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                loadingPb.setVisibility(View.GONE);
                return false;
            }
        }).transition(withCrossFade()).into(photoView);
        photoView.setOnClickListener(v -> {
            handler.removeCallbacks(hideIndicatorRunable);
            if (actionBarNormal.getTop() < 0) {
                showIndicator();
            } else {
                hideIndicator();
            }
        });
        handler.postDelayed(hideIndicatorRunable, 2500);
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Runnable hideIndicatorRunable = () -> hideIndicator();

    private void showIndicator() {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500);
        valueAnimator.setFloatValues(-1f, 0f);
        valueAnimator.addUpdateListener(animation -> {
            FrameLayout.LayoutParams actionBarNormalLayoutParams = (FrameLayout.LayoutParams) actionBarNormal.getLayoutParams();
            actionBarNormalLayoutParams.topMargin = (int) (((float) animation.getAnimatedValue()) * actionBarNormal.getMeasuredHeight());
            actionBarNormal.setLayoutParams(actionBarNormalLayoutParams);

            FrameLayout.LayoutParams fabBtnsLayoutParams = (FrameLayout.LayoutParams) fabBtns.getLayoutParams();
            fabBtnsLayoutParams.bottomMargin = (int) ((float) animation.getAnimatedValue() * fabBtns.getMeasuredHeight());
            fabBtns.setLayoutParams(fabBtnsLayoutParams);
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.postDelayed(hideIndicatorRunable, 2500);
            }
        });
        valueAnimator.start();
    }

    private void hideIndicator() {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(500);
        valueAnimator.setFloatValues(0f, -1f);
        valueAnimator.addUpdateListener(animation -> {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) actionBarNormal.getLayoutParams();
            lp.topMargin = (int) (actionBarNormal.getMeasuredHeight() * (float) animation.getAnimatedValue());
            actionBarNormal.setLayoutParams(lp);

            FrameLayout.LayoutParams fabBtnsLayoutParams = (FrameLayout.LayoutParams) fabBtns.getLayoutParams();
            fabBtnsLayoutParams.bottomMargin = (int) (fabBtns.getMeasuredHeight() * (float) animation.getAnimatedValue());
            fabBtns.setLayoutParams(fabBtnsLayoutParams);
        });
        valueAnimator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
