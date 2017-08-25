package com.yun.opern.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yun.opern.R;


/**
 * Created by 允儿 on 2016/8/29.
 */
public class ProgressDialog extends AlertDialog {
    private ProgressBar mProgress;
    private TextView mMessageView;



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
        View view = inflater.inflate(R.layout.layout_progress_dialog, null);
        mProgress = (ProgressBar) view.findViewById(R.id.layout_progress_dialog_progress);
        mMessageView = (TextView) view.findViewById(R.id.layout_progress_dialog_message);
        setView(view);
        super.onCreate(savedInstanceState);

    }


}
