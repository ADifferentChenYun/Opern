package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.fluidlayout.FluidLayout;
import com.yun.opern.Application;
import com.yun.opern.R;
import com.yun.opern.db.DBCore;
import com.yun.opern.db.SearchHistory;
import com.yun.opern.db.SearchHistoryDao;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.DisplayUtil;
import com.yun.opern.utils.KeyboardUtils;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.schedulers.NewThreadScheduler;

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
    FluidLayout searchHistoryView;

    private String searchParameter;
    private ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
    private Adapter adapter;
    private boolean requesting = false;
    private Disposable searchDisposable;

    private String[] searchHistoryBackGroundColor = new String[]{"#f8f2ec", "#f9eaeb", "#f2f2f2", "#f2f6e9",};
    private String[] searchHistoryStockColor = new String[]{"#f7cfac", "#f9b8bd", "#d4d4d4", "#cfdcb5"};

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
                if (s.length() == 0) {
                    searchHistoryView.setVisibility(View.VISIBLE);
                }
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
        initSearchHistoryView();
    }

    private void initSearchHistoryView() {
        searchHistoryView.removeAllViews();
        int max = 3;
        int min = 0;
        Random random = new Random();
        for (SearchHistory item : getSerchHistory()) {
            int i = random.nextInt(max) % (max - min + 1) + min;
            View view = getLayoutInflater().inflate(R.layout.item_search_history_layout, searchHistoryView, false);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(DisplayUtil.dp2px(context, 1), Color.parseColor(searchHistoryStockColor[i]));
            gradientDrawable.setColor(Color.parseColor(searchHistoryBackGroundColor[i]));
            gradientDrawable.setCornerRadius(DisplayUtil.dp2px(context, 4));
            view.setBackground(gradientDrawable);
            ((TextView) view.findViewById(R.id.search_history_tv)).setText(item.getSearchParameter());
            view.setOnClickListener(v -> {
                searchParameter = item.getSearchParameter();
                searchInputEdt.setText(item.getSearchParameter());
                searchInputEdt.setSelection(searchInputEdt.getText().length());
                net();
            });
            searchHistoryView.addView(view);
        }
    }

    private List<SearchHistory> getSerchHistory() {
        return DBCore.getInstance(Application.getAppContext()).getSearchHistoryDao().queryBuilder().orderDesc(SearchHistoryDao.Properties.Date).limit(15).build().list();
    }

    private void addSearchStr2History(String searchStr) {
        SearchHistory searchHistory = new SearchHistory(searchStr, new Date());
        DBCore.getInstance(Application.getAppContext()).getSearchHistoryDao().insertOrReplace(searchHistory);
    }

    public void net() {
        searchHistoryView.setVisibility(View.GONE);
        initSearchHistoryView();
        addSearchStr2History(searchParameter);
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
