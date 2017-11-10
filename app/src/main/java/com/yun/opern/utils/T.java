package com.yun.opern.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yun.opern.Application;
import com.yun.opern.R;


/**
 * Toast统一管理类
 * @author Yun
 */
public class T {
    private static Toast toast;

    static {
        LayoutInflater inflate = (LayoutInflater) Application.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflate.inflate(R.layout.layout_toast, null);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        toast = new Toast(Application.getAppContext());
        toast.setView(view);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, DisplayUtil.dp2px(Application.getAppContext(), 50));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setText("......");
    }

    public static void showShort(CharSequence message) {
        toast.setText(message);
        toast.show();
    }

    public static void hideToast() {
        if (null != toast) {
            toast.cancel();
        }
    }
}
