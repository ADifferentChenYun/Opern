package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yun.opern.R;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.NewThreadScheduler;

public class LastUpdateOpernInfoActivity extends BaseActivity {


    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.empty_view)
    View emptyView;

    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private Disposable disposable;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_last_update_opern_info;
    }

    @Override
    protected void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(opernInfoArrayList);
        opernLv.setAdapter(adapter);
        net();
    }

    private void net() {
        disposable = HttpCore.getInstance().getApi()
                .latestUpdateOpernInfo()
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    opernInfoArrayList.clear();
                    opernInfoArrayList.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    showProgressDialog(false);
                }, throwable -> {
                    ErrorMessageUtil.showErrorByToast(throwable);
                    showProgressDialog(false);
                });
        showProgressDialog(true, dialog -> {
            disposable.dispose();
        });
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private ArrayList<OpernInfo> opernInfoArrayList;

        public Adapter(ArrayList<OpernInfo> opernInfoArrayList) {
            this.opernInfoArrayList = opernInfoArrayList;
        }

        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_opern_list, parent, false));
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            OpernInfo opernInfo = opernInfoArrayList.get(position);
            holder.titleTv.setText(opernInfo.getTitle());
            holder.wordAuthorTv.setText("作词：" + opernInfo.getWordAuthor());
            holder.songAuthorTv.setText("作曲：" + opernInfo.getSongAuthor());
            holder.singerTv.setText("演唱：" + opernInfo.getSinger());
            holder.dataOriginTv.setText(opernInfo.getOrigin());
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
            holder.categoryTv.setText(stringBuilder.toString());
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
                    intent.putExtra("opernInfo", opernInfoArrayList.get(getLayoutPosition()));
                    startActivity(intent);
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
