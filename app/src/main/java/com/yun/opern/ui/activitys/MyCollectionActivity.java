package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SquareImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MyCollectionActivity extends BaseActivity {

    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.img_gv)
    GridView imgGv;
    @BindView(R.id.empty_view)
    View emptyView;

    private GridViewAdapter adapter;
    private ArrayList<OpernInfo> opernInfos = new ArrayList<>();

    @Override
    protected int contentViewRes() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void initView() {
        adapter = new GridViewAdapter(opernInfos);
        imgGv.setAdapter(adapter);
        imgGv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, ShowImageActivity.class);
            intent.putExtra("opernInfo", opernInfos.get(position));
            startActivity(intent);
        });
        getCollectedOpernInfo();
    }

    private void getCollectedOpernInfo() {
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if (weiBoUserInfo == null) {
            return;
        }
        showProgressDialog(true);
        HttpCore.getInstance().getApi()
                .getCollectionOpernInfo(weiBoUserInfo.getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    opernInfos.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    showProgressDialog(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    ErrorMessageUtil.showErrorByToast(throwable);
                    showProgressDialog(false);
                });
    }

    /**
     * 取消收藏
     */
    public void removeCollect(int position) {
        showProgressDialog(true);
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi()
                .removeCollection(weiBoUserInfo.getId(), opernInfos.get(position).getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                            if (baseResponse.isSuccess()) {
                                opernInfos.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                ErrorMessageUtil.showErrorByToast(baseResponse.getMessage());
                            }
                            showProgressDialog(false);
                        }, throwable -> {
                    ErrorMessageUtil.showErrorByToast(throwable);
                            showProgressDialog(false);
                        }
                );
    }

    public class GridViewAdapter extends BaseAdapter {
        private ArrayList<OpernInfo> opernInfos;
        private ViewHolder viewHolder;

        public GridViewAdapter(ArrayList<OpernInfo> opernInfos) {
            this.opernInfos = opernInfos;
        }

        @Override
        public int getCount() {
            emptyView.setVisibility(opernInfos.size() == 0 ? View.VISIBLE : View.GONE);
            return opernInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return opernInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_img_gv_layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final OpernInfo opernInfo = opernInfos.get(position);
            viewHolder.itemImgGvLayoutTv.setText(opernInfo.getTitle());
            Glide.with(context).asBitmap().load(opernInfo.getImgs().get(0).getOpernImg()).transition(withCrossFade()).into(viewHolder.itemImgGvLayoutImg);
            viewHolder.deleteImg.setOnClickListener((view) -> removeCollect(position));
            return convertView;
        }


        public class ViewHolder {
            @BindView(R.id.item_img_gv_layout_img)
            SquareImageView itemImgGvLayoutImg;
            @BindView(R.id.item_img_gv_layout_tv)
            TextView itemImgGvLayoutTv;
            @BindView(R.id.item_img_gv_delete_img)
            ImageView deleteImg;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
