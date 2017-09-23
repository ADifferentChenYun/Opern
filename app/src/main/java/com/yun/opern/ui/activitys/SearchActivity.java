package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.fluidlayout.FluidLayout;
import com.yun.opern.R;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.KeyboardUtils;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SearchView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.NewThreadScheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.search_input_edt)
    EditText searchInputEdt;
    @BindView(R.id.search_btn)
    ImageView searchBtn;
    @BindView(R.id.opern_lv)
    RecyclerView opernLv;
    @BindView(R.id.progressbar)
    ProgressBar progressBar;
    @BindView(R.id.search_history)
    FluidLayout searchHistory;

    private String searchParameter;
    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private boolean requesting = false;
    private Disposable searchDisposable;

    @Override
    protected int contentViewRes() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        searchInputEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBtn.callOnClick();
            }
            return false;
        });
        searchInputEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        opernLv.setLayoutManager(linearLayoutManager);
        opernLv.setItemAnimator(new DefaultItemAnimator());
        adapter = new Adapter(opernInfoArrayList);
        opernLv.setAdapter(adapter);
        searchBtn.setOnClickListener(v -> {
            if(requesting){
                return;
            }
            searchParameter = searchInputEdt.getText().toString().trim();
            if (searchParameter.equals("")) {
                return;
            }
            net();
        });

    }

    public void net() {
        KeyboardUtils.hideSoftInput(searchInputEdt);
        requesting = true;
        opernLv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        searchDisposable = HttpCore.getInstance().getApi()
                .searchOpernInfo(searchParameter)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    opernInfoArrayList.clear();
                    ArrayList<OpernInfo> data = arrayListBaseResponse.getData();
                    opernInfoArrayList.addAll(data);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    opernLv.setVisibility(View.VISIBLE);
                    requesting = false;
                }, throwable -> {
                    throwable.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    opernLv.setVisibility(View.VISIBLE);
                    T.showShort("网络异常");
                    requesting = false;
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchDisposable != null) {
            searchDisposable.dispose();
        }
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
            @BindView(R.id.item_opern_list_data_category_tv)
            TextView categoryTv;

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
