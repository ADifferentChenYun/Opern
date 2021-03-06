package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.event.ReceiveMessageFromJPushEvent;
import com.yun.opern.model.event.UserLoginOrLogoutEvent;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.ui.fragments.CategoryFragment;
import com.yun.opern.ui.fragments.HomeFragment;
import com.yun.opern.utils.DisplayUtil;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.ScreenUtils;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.ViewPagerFix;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MainActivity extends BaseActivity {


    @BindView(R.id.search_fab)
    FloatingActionButton searchFab;
    @BindView(R.id.actionbar)
    public ActionBarNormal actionbar;
    @BindView(R.id.view_pager)
    ViewPagerFix viewPager;
    @BindView(R.id.home_index)
    LinearLayout homeIndex;
    @BindView(R.id.category_index)
    LinearLayout categoryIndex;
    @BindView(R.id.indicator)
    View indicator;

    private ViewPagerAdapter viewPagerAdapter;
    private int searchFabInitBottomMargin;

    @Override
    protected int contentViewRes() {
        EventBus.getDefault().register(this);
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (weiBoUserInfo != null) {
            Glide.with(this).asBitmap().load(WeiBoUserInfoKeeper.read(context).getAvatar_hd()).transition(withCrossFade()).into(actionbar.getMoreButton());
            actionbar.getMoreButton().setBorderWidth(DisplayUtil.px2dp(context, 2));
        }
        actionbar.showBackButton(false);
        actionbar.showTitle(true);
        actionbar.showMoreButton(true);
        actionbar.getMoreButton().setOnClickListener((view) -> startActivity(new Intent(MainActivity.this, MoreActivity.class)));

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                lp.width = ScreenUtils.getScreenWidth() / 2;
                lp.leftMargin = (int) (ScreenUtils.getScreenWidth() / 2 * position + ScreenUtils.getScreenWidth() / 2 * positionOffset);
                indicator.setLayoutParams(lp);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) searchFab.getLayoutParams();
                layoutParams.bottomMargin = searchFabInitBottomMargin - (int) ((searchFabInitBottomMargin + searchFab.getMeasuredHeight()) * (position + positionOffset));
                searchFab.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        searchFabInitBottomMargin = ((FrameLayout.LayoutParams) searchFab.getLayoutParams()).bottomMargin;
    }

    @Override
    protected void initedView() {
        super.initedView();
        RxView.clicks(searchFab)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> startActivity(new Intent(context, SearchActivity.class)));
        RxView.clicks(homeIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(0, true));
        RxView.clicks(categoryIndex)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> viewPager.setCurrentItem(1, true));
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new HomeFragment());
            fragments.add(new CategoryFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginOrLogout(UserLoginOrLogoutEvent userLoginOrLogoutEvent) {
        if (userLoginOrLogoutEvent.isLogin()) {
            Glide.with(this).asBitmap().load(WeiBoUserInfoKeeper.read(context).getAvatar_hd()).transition(withCrossFade()).into(actionbar.getMoreButton());
            actionbar.getMoreButton().setBorderWidth(DisplayUtil.px2dp(context, 2));
        } else {
            actionbar.getMoreButton().setImageResource(R.mipmap.ic_more);
            actionbar.getMoreButton().setBorderWidth(0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMessageFromJPush(ReceiveMessageFromJPushEvent receiveMessageFromJPushEvent) {
        showDialog("开发者消息", receiveMessageFromJPushEvent.getMessage(), "嗯,知道了", (dialog, which) -> {
        });
    }


    private long currentTime = 0;

    /**
     * 双击退出整个应用(间隔3s)，MainActivity launchMode 设置为singleTop
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() / 1000 - currentTime < 3) {
            finish(); //结束当前activity
            System.exit(0); //系统退出
        } else {
            ErrorMessageUtil.showErrorByToast("再次点击退出应用");
            currentTime = System.currentTimeMillis() / 1000;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
