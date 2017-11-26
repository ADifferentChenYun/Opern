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
import com.yun.opern.ui.activitys.ShowImageActivity;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.views.ActionBarNormal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


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


    public void setAnimView(ActionBarNormal actionBarNormal, LinearLayout fabBtns) {
        this.actionBarNormal = actionBarNormal;
        this.fabBtns = fabBtns;
        if (this.actionBarNormal == null) {
            this.actionBarNormal = ((ShowImageActivity) getActivity()).getActionbar();
        }
        if (this.fabBtns == null) {
            this.fabBtns = ((ShowImageActivity) getActivity()).getFabBtns();
        }
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
                    ErrorMessageUtil.showErrorByToast("加载图片失败，正在重新加载");
                } else {
                    ErrorMessageUtil.showErrorByToast("加载图片失败，请重试");
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

    private ValueAnimator showAnimator;

    private void showIndicator() {
        showAnimator = new ValueAnimator();
        showAnimator.setDuration(500);
        showAnimator.setFloatValues(-1f, 0f);
        showAnimator.addUpdateListener(animation -> {
            if (actionBarNormal == null || fabBtns == null) {
                return;
            }
            FrameLayout.LayoutParams actionBarNormalLayoutParams = (FrameLayout.LayoutParams) actionBarNormal.getLayoutParams();
            actionBarNormalLayoutParams.topMargin = (int) (((float) animation.getAnimatedValue()) * actionBarNormal.getMeasuredHeight());
            actionBarNormal.setLayoutParams(actionBarNormalLayoutParams);

            FrameLayout.LayoutParams fabBtnsLayoutParams = (FrameLayout.LayoutParams) fabBtns.getLayoutParams();
            fabBtnsLayoutParams.bottomMargin = (int) ((float) animation.getAnimatedValue() * fabBtns.getMeasuredHeight());
            fabBtns.setLayoutParams(fabBtnsLayoutParams);
        });
        showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.postDelayed(hideIndicatorRunable, 2500);
            }
        });
        showAnimator.start();
    }

    private ValueAnimator hideAnimator;

    private void hideIndicator() {
        hideAnimator = new ValueAnimator();
        hideAnimator.setDuration(500);
        hideAnimator.setFloatValues(0f, -1f);
        hideAnimator.addUpdateListener(animation -> {
            if (actionBarNormal == null || fabBtns == null) {
                return;
            }
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) actionBarNormal.getLayoutParams();
            lp.topMargin = (int) (actionBarNormal.getMeasuredHeight() * (float) animation.getAnimatedValue());
            actionBarNormal.setLayoutParams(lp);

            FrameLayout.LayoutParams fabBtnsLayoutParams = (FrameLayout.LayoutParams) fabBtns.getLayoutParams();
            fabBtnsLayoutParams.bottomMargin = (int) (fabBtns.getMeasuredHeight() * (float) animation.getAnimatedValue());
            fabBtns.setLayoutParams(fabBtnsLayoutParams);
        });
        hideAnimator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (hideAnimator != null) {
            hideAnimator.cancel();
            hideAnimator = null;
        }
        if (showAnimator != null) {
            showAnimator.cancel();
            showAnimator = null;
        }
    }
}
