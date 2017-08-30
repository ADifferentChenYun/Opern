package com.yun.opern.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yun.opern.R;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by 允儿 on 2016/8/30.
 */
public class ActionBarNormal extends FrameLayout {
    private ImageView backImg;
    private CircleImageView moreImg;
    private TextView titleTv;
    private String titleStr = "this is title";
    private int titleColor;
    private BackButtonStyle style = BackButtonStyle.StyleOne;
    private boolean showTitle, showBackButton, showMoreButton;

    public enum BackButtonStyle {
        StyleOne,
        StyleTwo
    }

    public ActionBarNormal(Context context) {
        super(context);
        initView();
    }

    public ActionBarNormal(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionBarNormal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActionBarNormal, defStyleAttr, 0);

        style = a.getInt(R.styleable.ActionBarNormal_backButtonStyle, 0) == 0 ? BackButtonStyle.StyleOne : BackButtonStyle.StyleTwo;
        showTitle = a.getBoolean(R.styleable.ActionBarNormal_showTitle, true);
        showBackButton = a.getBoolean(R.styleable.ActionBarNormal_showBackButton, true);
        showMoreButton = a.getBoolean(R.styleable.ActionBarNormal_showMoreButton, false);
        titleStr = a.getString(R.styleable.ActionBarNormal_title);
        titleColor = a.getColor(R.styleable.ActionBarNormal_titleColor, getResources().getColor(android.R.color.white));
        a.recycle();
        initView();
    }

    private void initView() {

        Typeface tf = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/roboto-mono-regular.ttf");//根据路径得到Typeface
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_actionbar_normal, this, false);
        backImg = (ImageView) view.findViewById(R.id.layout_actionbar_normal_back_img);
        moreImg = (CircleImageView) view.findViewById(R.id.layout_actionbar_normal_more_img);
        titleTv = (TextView) view.findViewById(R.id.layout_actionbar_normal_title_tv);
        titleTv.setTypeface(tf);//设置字体
        titleTv.setTextColor(titleColor);
        titleTv.setText(titleStr);
        showTitle(showTitle);
        showBackButton(showBackButton);
        showMoreButton(showMoreButton);
        setBackButtonStyle(style);
        backImg.setOnClickListener(v -> ((AppCompatActivity) getContext()).finish());
        addView(view);
    }

    public void transparent(boolean b) {
        setAlpha(b ? 0f : 1f);
    }

    public void setVisibility(boolean showBack, boolean showTitle, boolean showMore) {
        showBackButton(showBack);
        showTitle(showTitle);
        showMoreButton(showBack);
    }

    public void showTitle(boolean show) {
        titleTv.setVisibility(show ? VISIBLE : GONE);
    }

    public void showBackButton(boolean show) {
        backImg.setVisibility(show ? VISIBLE : GONE);
    }

    public void showMoreButton(boolean show) {
        moreImg.setVisibility(show ? VISIBLE : GONE);
    }

    public void setBackButtonStyle(BackButtonStyle backButtonStyle) {
        backImg.setImageDrawable(backButtonStyle == BackButtonStyle.StyleOne ? getResources().getDrawable(R.mipmap.ic_back) : getResources().getDrawable(R.mipmap.ic_back_two));
    }

    public CircleImageView getMoreButton(){
        return moreImg;
    }

    public void setTitle(String titleStr){
        titleTv.setText(titleStr);
    }
}
