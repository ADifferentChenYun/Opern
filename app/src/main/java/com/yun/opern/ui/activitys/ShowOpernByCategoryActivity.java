package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yun.opern.R;
import com.yun.opern.model.CategoryInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class ShowOpernByCategoryActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.category_opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.category_opern_srl)
    SwipeRefreshLayout opernSrl;
    @BindView(R.id.empty_view)
    View emptyView;

    private CategoryInfo categoryAll = new CategoryInfo("全部", null);
    private CategoryInfo categoryOne;
    private CategoryInfo categoryTwo;

    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int index = 0;
    private boolean requesting = false;
    private Disposable disposable;

    @Override
    protected int contentViewRes() {
        if (getIntent().getExtras().containsKey("categoryOne")) {
            categoryOne = (CategoryInfo) getIntent().getExtras().get("categoryOne");
        }
        if (getIntent().getExtras().containsKey("categoryTwo")) {
            categoryTwo = (CategoryInfo) getIntent().getExtras().get("categoryTwo");
        }
        return R.layout.activity_show_opern_by_category;
    }

    @Override
    protected void initView() {
        if (categoryOne == null) {
            T.showShort("发送了一个错误");
            finish();
            return;
        }
        if (categoryTwo == null) {
            actionbar.setTitle(categoryOne.getCategory());
        } else {
            actionbar.setTitle(categoryOne.getCategory() + "-" + categoryTwo.getCategory());
        }
        actionbar.showMoreButton(false);

        linearLayoutManager = new LinearLayoutManager(context);
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
                            getOpernInfoByCategory();
                        }
                    }
                }
            }
        });
        opernSrl.setColorSchemeColors(getResources().getColor(R.color.light_blue));
        opernSrl.setOnRefreshListener(() -> {
            index = 0;
            getOpernInfoByCategory();
        });
        getOpernInfoByCategory();
    }


    private void getOpernInfoByCategory() {
        requesting = true;
        opernSrl.setRefreshing(true);
        int numPrePage = 40;
        String categoryOneStr = categoryOne.getCategory();
        String categoryTwoStr = categoryTwo == null ? null : categoryTwo.getCategory();
        disposable = HttpCore.getInstance().getApi().searchOpernInfoByCategory(categoryOneStr, categoryTwoStr, index, numPrePage)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private ArrayList<OpernInfo> opernInfoArrayList;


        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Adapter.ViewHolder(getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder viewHolder, int position) {
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
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("opernInfo", opernInfoArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }
}
