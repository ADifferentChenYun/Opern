package com.yun.opern.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by 允儿 on 2016/8/12.
 */
public class SquareImageView extends android.support.v7.widget.AppCompatImageView{

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
