package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yun.opern.BuildConfig;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.model.UpdateInfo;
import com.yun.opern.model.event.UserLoginOrLogoutEvent;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.DisplayUtil;
import com.yun.opern.utils.SPUtil;
import com.yun.opern.utils.T;
import com.yun.opern.utils.UpdateAsync;
import com.yun.opern.views.ActionBarNormal;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MainActivity extends BaseActivity {

    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.opern_srl)
    SwipeRefreshLayout opernSrl;
    @BindView(R.id.search_fab)
    FloatingActionButton searchFab;
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.empty_view)
    View emptyView;

    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int index = 0;
    private boolean requesting = false;


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
        actionbar.setOnDoubleClickListener(view -> opernLv.smoothScrollToPosition(0));
        linearLayoutManager = new LinearLayoutManager(this);
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(opernInfoArrayList);
        opernLv.setAdapter(adapter);
        opernLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    int totalItemCount = opernInfoArrayList.size();
                    if (lastVisibleItem >= totalItemCount - 10) {
                        if (!requesting) {
                            net();
                        }
                    }
                }
            }
        });
        opernSrl.setColorSchemeColors(getResources().getColor(R.color.light_blue));
        opernSrl.setOnRefreshListener(() -> {
            index = 0;
            net();
        });
        RxView.clicks(searchFab).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> startActivity(new Intent(context, SearchActivity.class)));
    }

    @Override
    protected void initedView() {
        super.initedView();
        net();
        new Handler().postDelayed(() -> checkVersion(), 1000);
    }

    private void net() {
        requesting = true;
        opernSrl.setRefreshing(true);
        int numPrePage = 40;
        HttpCore.getInstance().getApi()
                .getPopOpernInfo(index, numPrePage)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    if (index == 0) {
                        opernInfoArrayList.clear();
                    }
                    ArrayList<OpernInfo> data = arrayListBaseResponse.getData();
                    if (data == null || data.size() == 0) {
                        T.showShort("没有更多数据了");
                    } else {
                        opernInfoArrayList.addAll(data);
                        index++;
                    }
                    adapter.notifyDataSetChanged();
                    opernSrl.setRefreshing(false);
                    requesting = false;
                }, throwable -> {
                    throwable.printStackTrace();
                    opernSrl.setRefreshing(false);
                    requesting = false;
                    T.showShort(throwable.getMessage());
                });
    }

    private void checkVersion() {
        HttpCore.getInstance().getApi().checkVersion()
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateInfoBaseResponse -> {
                    UpdateInfo updateInfo = updateInfoBaseResponse.getData();
                    if (updateInfo.getVersionCode() > BuildConfig.VERSION_CODE) {
                        if (updateInfo.getUpdateType().equals("0") && updateInfo.getVersionCode() == SPUtil.getInt(SPUtil.Update_No_Longer_Reminded_Key, 0)) {
                            return;
                        }
                        //存在更新
                        StringBuilder sb = new StringBuilder();
                        sb.append("当前版本：");
                        sb.append(BuildConfig.VERSION_NAME);
                        sb.append("\n");
                        sb.append("最新版本：");
                        sb.append(updateInfo.getVersionName());
                        sb.append("\n");
                        sb.append("更新类型：");
                        sb.append(updateInfo.getUpdateType().equals("0") ? "推荐更新" : "强制更新");
                        sb.append("\n");
                        sb.append("更新时间：");
                        sb.append(updateInfo.getUpdateDataTime());
                        sb.append("\n");
                        sb.append("更新信息：");
                        sb.append(updateInfo.getUpdateMessage());
                        sb.append("\n");
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("更新")
                                .setMessage(sb.toString())
                                .setCancelable(false)
                                .setPositiveButton("更新", (dialog, which) -> {
                                    UpdateAsync updateAsync = new UpdateAsync(context);
                                    updateAsync.execute(HttpCore.baseUrl + updateInfo.getDownloadUrl());
                                });
                        if (updateInfo.getUpdateType().equals("0")) {
                            builder.setCancelable(true);
                            builder.setNegativeButton("下次更新", null);
                            builder.setNeutralButton("不再提醒", (dialog, which) -> SPUtil.putInt(SPUtil.Update_No_Longer_Reminded_Key, updateInfo.getVersionCode()));
                        }
                        builder.create().show();
                    }
                }, t -> {
                    t.printStackTrace();
                    T.showShort(t.getMessage());
                });
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

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private ArrayList<OpernInfo> opernInfoArrayList;


        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            OpernInfo opernInfo = opernInfoArrayList.get(position);
            viewHolder.titleTv.setText(opernInfo.getTitle());
            viewHolder.wordAuthorTv.setText("作词：" + opernInfo.getWordAuthor());
            viewHolder.songAuthorTv.setText("作曲：" + opernInfo.getSongAuthor());
            viewHolder.singerTv.setText("演唱：" + opernInfo.getSinger());
            viewHolder.dataOriginTv.setText(opernInfo.getOrigin());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(opernInfo.getCategoryOne());
            if (!opernInfo.getCategoryTwo().equals("")) {
                stringBuilder.append("/");
                stringBuilder.append(opernInfo.getCategoryTwo());
            }
            if (!opernInfo.getCategoryThree().equals("")) {
                stringBuilder.append("/");
                stringBuilder.append(opernInfo.getCategoryThree());
            }
            viewHolder.categoryTv.setText(stringBuilder.toString());
        }

        @Override
        public int getItemCount() {
            emptyView.setVisibility(opernInfoArrayList.size() == 0 ? View.VISIBLE : View.GONE);
            return opernInfoArrayList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_opern_list_title_tv)
            TextView titleTv;
            @BindView(R.id.item_opern_list_wordAuthor_tv)
            TextView wordAuthorTv;
            @BindView(R.id.item_opern_list_songAuthor_tv)
            TextView songAuthorTv;
            @BindView(R.id.item_opern_list_singer_tv)
            TextView singerTv;
            @BindView(R.id.item_opern_list_data_origin_tv)
            TextView dataOriginTv;
            @BindView(R.id.item_opern_list_data_category_tv)
            TextView categoryTv;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ShowImageActivity.class);
                    intent.putExtra("opernInfo", opernInfoArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
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
            T.showShort("再次点击退出应用");
            currentTime = System.currentTimeMillis() / 1000;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
