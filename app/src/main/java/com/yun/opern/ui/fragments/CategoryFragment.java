package com.yun.opern.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.yun.opern.utils.T;

import java.util.ArrayList;

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
                }, t -> T.showShort(t.getMessage()));
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private ArrayList<CategoryInfo> categoryInfos;


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
            for (CategoryInfo categoryTwo : categoryInfo.getCategoryInfos()) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.item_category_two_layout, null);
                ((TextView) view.findViewById(R.id.category_two_tv)).setText(categoryTwo.getCategory());
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
