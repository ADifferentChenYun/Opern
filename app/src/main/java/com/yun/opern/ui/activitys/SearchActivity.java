package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yun.opern.R;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.search_input_edt)
    EditText searchInputEdt;
    @BindView(R.id.search_btn)
    SearchView searchBtn;
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;

    private String searchParameter;
    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private boolean requesting = false;


    @Override
    protected int contentViewRes() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        linearLayoutManager = new LinearLayoutManager(this);
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(opernInfoArrayList);
        opernLv.setAdapter(adapter);
        searchBtn.setOnClickListener(v -> {
            if(requesting){
                return;
            }
            searchParameter = searchInputEdt.getText().toString().trim();
            net();
        });

    }

    public void net() {
        requesting = true;
        searchBtn.start();
        HttpCore.getInstance().getApi().searchOpernInfo(searchParameter).enqueue(new Callback<BaseResponse<ArrayList<OpernInfo>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<OpernInfo>>> call, Response<BaseResponse<ArrayList<OpernInfo>>> response) {
                opernInfoArrayList.clear();
                ArrayList<OpernInfo> data = response.body().getData();
                opernInfoArrayList.addAll(data);
                adapter.notifyDataSetChanged();
                searchBtn.end();
                requesting = false;
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<OpernInfo>>> call, Throwable t) {
                t.printStackTrace();
                searchBtn.end();
                T.showShort("网络异常");
                requesting = false;
            }
        });
    }

    public class Adapter extends RecyclerView.Adapter<SearchActivity.Adapter.ViewHolder> {
        private ArrayList<OpernInfo> opernInfoArrayList;


        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public SearchActivity.Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(SearchActivity.Adapter.ViewHolder viewHolder, int position) {
            OpernInfo opernInfo = opernInfoArrayList.get(position);
            viewHolder.titleTv.setText(opernInfo.getTitle());
            viewHolder.wordAuthorTv.setText("作词：" + opernInfo.getWordAuthor());
            viewHolder.songAuthorTv.setText("作曲：" + opernInfo.getSongAuthor());
            viewHolder.singerTv.setText("演唱：" + opernInfo.getSinger());
            viewHolder.dataOriginTv.setText(opernInfo.getDataOrigin());
        }

        @Override
        public int getItemCount() {
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchActivity.this, ShowImageActivity.class);
                    intent.putExtra("opernInfo",opernInfoArrayList.get(getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }
}
