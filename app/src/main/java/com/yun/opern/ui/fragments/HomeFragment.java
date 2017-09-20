package com.yun.opern.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yun.opern.R;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.activitys.MainActivity;
import com.yun.opern.ui.activitys.ShowImageActivity;
import com.yun.opern.utils.T;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;


public class HomeFragment extends Fragment {
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.opern_srl)
    SwipeRefreshLayout opernSrl;
    @BindView(R.id.empty_view)
    View emptyView;

    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int index = 0;
    private boolean requesting = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        ((MainActivity) getActivity()).actionbar.setOnDoubleClickListener(view -> opernLv.smoothScrollToPosition(0));
        linearLayoutManager = new LinearLayoutManager(getActivity());
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
        net();
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


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private ArrayList<OpernInfo> opernInfoArrayList;


        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
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
                    Intent intent = new Intent(getActivity(), ShowImageActivity.class);
                    intent.putExtra("opernInfo", opernInfoArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }

}
