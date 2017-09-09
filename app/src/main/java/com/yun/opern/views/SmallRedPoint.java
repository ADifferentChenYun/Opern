package com.yun.opern.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


/**
 * Created by 允儿 on 2016/1/20 0020.
 * 小红点
 */
public class SmallRedPoint extends View {
    private Paint paint;

    public SmallRedPoint(Context context) {
        super(context);
        initPaint();
    }

    public SmallRedPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public SmallRedPoint(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
    }

}
