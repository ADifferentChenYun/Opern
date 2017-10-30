package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.model.NetEaseCloudMusicChartInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MusicChartActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.music_chart_lv)
    ListView musicChartLv;
    private Adapter adapter;
    private ArrayList<NetEaseCloudMusicChartInfo> data = new ArrayList<>();

    @Override
    protected int contentViewRes() {
        return R.layout.activity_music_chart;
    }

    @Override
    protected void initView() {
        adapter = new Adapter(data);
        musicChartLv.setAdapter(adapter);
        getMusicChartFromNet();
    }

    public void getMusicChartFromNet() {
        showProgressDialog(true);
        HttpCore.getInstance().getApi().musicChart().subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    data.clear();
                    data.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    showProgressDialog(false);
                }, throwable -> {
                    showProgressDialog(false);
                    ErrorMessageUtil.showErrorByToast(throwable);
                });
        musicChartLv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, MusicChartDetailActivity.class);
            intent.putExtra("netEaseCloudMusicChartInfo", data.get(position));
            startActivity(intent);
        });
    }

    public class Adapter extends BaseAdapter {
        private ArrayList<NetEaseCloudMusicChartInfo> data;
        private ViewHolder viewHolder;

        public Adapter(ArrayList<NetEaseCloudMusicChartInfo> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_music_chart_layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NetEaseCloudMusicChartInfo info = data.get(position);
            Glide.with(context).asBitmap().load(info.getImg()).transition(withCrossFade()).into(viewHolder.musicChartImg);
            viewHolder.musicChartNameTv.setText(info.getName());
            viewHolder.musicChartUpdateTimeInfoTv.setText(info.getUpdateTimeInfo());
            return convertView;
        }

        public class ViewHolder {
            @BindView(R.id.music_chart_img)
            ImageView musicChartImg;
            @BindView(R.id.music_chart_name_tv)
            TextView musicChartNameTv;
            @BindView(R.id.music_chart_update_time_info_tv)
            TextView musicChartUpdateTimeInfoTv;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
