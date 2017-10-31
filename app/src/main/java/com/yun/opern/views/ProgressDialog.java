package com.yun.opern.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yun.opern.R;
import com.yun.opern.utils.DisplayUtil;


/**
 * Created by 允儿 on 2016/8/29.
 */
public class ProgressDialog extends AlertDialog {
    private AVLoadingIndicatorView mProgress;
    private TextView mMessageView;


    public ProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);
        initView();
    }

    private void initView() {
        mProgress = (AVLoadingIndicatorView) findViewById(R.id.layout_progress_dialog_progress);
        mMessageView = (TextView) findViewById(R.id.layout_progress_dialog_message);
        mProgress.post(() -> {
            Window win = getWindow();
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = DisplayUtil.dp2px(getContext(), 220);
            win.setAttributes(lp);
        });
        mProgress.smoothToShow();
    }

}
