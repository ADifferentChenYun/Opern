package com.yun.opern.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fynn.fluidlayout.FluidLayout;
import com.yun.opern.R;
import com.yun.opern.model.CategoryInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.activitys.ShowOpernByCategoryActivity;
import com.yun.opern.utils.DisplayUtil;
import com.yun.opern.utils.ErrorMessageUtil;

import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;


public class CategoryFragment extends Fragment {
    private Unbinder unbind;

    @BindView(R.id.fragment_category_lv)
    RecyclerView categoryLv;
    @BindView(R.id.empty_view)
    View empty_view;
    @BindView(R.id.fragment_category_srl)
    SwipeRefreshLayout categorySrl;

    private Adapter adapter;
    private ArrayList<CategoryInfo> categoryInfos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        unbind = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        categorySrl.setColorSchemeColors(getResources().getColor(R.color.light_blue));
        categorySrl.setOnRefreshListener(this::getCategoryInfo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        categoryLv.setLayoutManager(linearLayoutManager);
        adapter = new Adapter(categoryInfos);
        categoryLv.setAdapter(adapter);
        getCategoryInfo();
    }

    private void getCategoryInfo() {
        HttpCore.getInstance().getApi().getCategoryInfo()
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    categoryInfos.clear();
                    categoryInfos.addAll(baseResponse.getData());
                    adapter.notifyDataSetChanged();
                    categorySrl.setRefreshing(false);
                    categorySrl.setEnabled(categoryInfos.size() == 0);
                }, t -> {
                    ErrorMessageUtil.showErrorByToast(t.getMessage());
                    categorySrl.setRefreshing(false);
                    categorySrl.setEnabled(categoryInfos.size() == 0);
                });
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private ArrayList<CategoryInfo> categoryInfos;
        private String[] searchHistoryBackGroundColor = new String[]{"#f8f2ec", "#f9eaeb", "#f2f2f2", "#f2f6e9",};
        private String[] searchHistoryStockColor = new String[]{"#f7cfac", "#f9b8bd", "#d4d4d4", "#cfdcb5"};

        public Adapter(ArrayList<CategoryInfo> categoryInfos) {
            this.categoryInfos = categoryInfos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_category_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            CategoryInfo categoryInfo = categoryInfos.get(position);
            viewHolder.categoryOneTv.setText(categoryInfo.getCategory());
            viewHolder.categoryTwoFluidLayout.removeAllViews();
            int max = 3;
            int min = 0;
            Random random = new Random();
            for (CategoryInfo categoryTwo : categoryInfo.getCategoryInfos()) {
                int i = random.nextInt(max) % (max - min + 1) + min;
                View view = getActivity().getLayoutInflater().inflate(R.layout.item_search_history_layout, viewHolder.categoryTwoFluidLayout, false);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setStroke(DisplayUtil.dp2px(getActivity(), 1), Color.parseColor(searchHistoryStockColor[i]));
                gradientDrawable.setColor(Color.parseColor(searchHistoryBackGroundColor[i]));
                gradientDrawable.setCornerRadius(DisplayUtil.dp2px(getActivity(), 4));
                view.setBackground(gradientDrawable);
                ((TextView) view.findViewById(R.id.search_history_tv)).setText(categoryTwo.getCategory());
                view.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), ShowOpernByCategoryActivity.class);
                    intent.putExtra("categoryOne", categoryInfo);
                    intent.putExtra("categoryTwo", categoryTwo);
                    startActivity(intent);
                });
                viewHolder.categoryTwoFluidLayout.addView(view);
            }
        }

        @Override
        public int getItemCount() {
            empty_view.setVisibility(categoryInfos.size() == 0 ? View.VISIBLE : View.GONE);
            return categoryInfos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.category_one_tv)
            TextView categoryOneTv;
            @BindView(R.id.category_two_fluid_layout)
            FluidLayout categoryTwoFluidLayout;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), ShowOpernByCategoryActivity.class);
                    intent.putExtra("categoryOne", categoryInfos.get(this.getAdapterPosition()));
                    startActivity(intent);
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbind.unbind();
    }
}
