package com.yun.opern.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.wang.avi.AVLoadingIndicatorView;
import com.yun.opern.R;
import com.yun.opern.utils.DisplayUtil;


/**
 * Created by 允儿 on 2016/8/29.
 */
public class ProgressDialog extends AlertDialog {
    private AVLoadingIndicatorView mProgress;
    private TextView mMessageView;
    private View view;
    private View decorView;


    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.layout_progress_dialog, null);
        mProgress = (AVLoadingIndicatorView) view.findViewById(R.id.layout_progress_dialog_progress);
        mMessageView = (TextView) view.findViewById(R.id.layout_progress_dialog_message);
        setView(view);
        decorView = getWindow().getDecorView();
        view.post(() -> {
            Window win = getWindow();
            WindowManager.LayoutParams lp = win.getAttributes();
            Logger.i(view.getWidth() + "");
            lp.width = DisplayUtil.dp2px(getContext(), 200);
            win.setAttributes(lp);
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        super.show();
        //view.setAlpha(0f);
        //view.animate().alpha(1f).setDuration(1000).start();
        mProgress.smoothToShow();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mProgress.smoothToHide();
    }

    @Override
    public void cancel() {
        super.cancel();
        mProgress.smoothToHide();
    }
}
