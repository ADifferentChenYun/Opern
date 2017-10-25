package com.yun.opern.ui.activitys;

import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.model.NetEaseCloudMusicChartInfo;
import com.yun.opern.model.NetEaseCloudMusicChartMusicInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.L;
import com.yun.opern.utils.T;
import com.yun.opern.utils.TimeUtils;
import com.yun.opern.views.ActionBarNormal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MusicChartDetailActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionBarNormal;
    @BindView(R.id.music_chart_detail_lv)
    ListView musicChartDetailLv;

    private ImageView musicChartImg;
    private TextView musicChartNameTv;
    private TextView musicChartUpdateTimeTv;

    private NetEaseCloudMusicChartInfo netEaseCloudMusicChartInfo;
    private List<NetEaseCloudMusicChartMusicInfo> netEaseCloudMusicChartMusicInfoList = new ArrayList<>();
    private Adapter adapter;
    private View headerView;

    private MediaPlayer mediaPlayer;

    @Override
    protected int contentViewRes() {
        netEaseCloudMusicChartInfo = (NetEaseCloudMusicChartInfo) getIntent().getExtras().get("netEaseCloudMusicChartInfo");
        return R.layout.activity_music_chart_detail;
    }

    @Override
    protected void initView() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            ErrorMessageUtil.showErrorByToast("播放错误");
            return false;
        });
        adapter = new Adapter(netEaseCloudMusicChartMusicInfoList);
        musicChartDetailLv.setAdapter(adapter);
        initListViewHeaderView();
        musicChartDetailLv.setOnItemClickListener((parent, view, position, id) -> {
            try {
                mediaPlayer.setDataSource(netEaseCloudMusicChartMusicInfoList.get(getRealPosition(position)).getPlayUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        net();
    }

    private int getRealPosition(int position) {
        return position - musicChartDetailLv.getHeaderViewsCount();
    }

    private void initListViewHeaderView() {
        headerView = getLayoutInflater().inflate(R.layout.header_music_chart_detail_lv, null);
        musicChartImg = (ImageView) headerView.findViewById(R.id.music_chart_img);
        musicChartNameTv = (TextView) headerView.findViewById(R.id.music_chart_name_tv);
        musicChartUpdateTimeTv = (TextView) headerView.findViewById(R.id.music_chart_update_time_tv);

        Glide.with(context).asBitmap().load(netEaseCloudMusicChartInfo.getImg()).transition(withCrossFade()).into(musicChartImg);
        musicChartNameTv.setText(netEaseCloudMusicChartInfo.getName());
        musicChartUpdateTimeTv.setText(netEaseCloudMusicChartInfo.getUpdateTimeInfo());
        musicChartDetailLv.addHeaderView(headerView);
    }

    private void net() {
        showProgressDialog(true);
        HttpCore.getInstance().getApi()
                .musicChartMusic(netEaseCloudMusicChartInfo.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(new NewThreadScheduler())
                .subscribe(arrayListBaseResponse -> {
                    netEaseCloudMusicChartMusicInfoList.clear();
                    netEaseCloudMusicChartMusicInfoList.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    showProgressDialog(false);
                }, throwable -> {
                    showProgressDialog(false);
                    ErrorMessageUtil.showErrorByToast(throwable);
                });
    }

    public class Adapter extends BaseAdapter {
        private List<NetEaseCloudMusicChartMusicInfo> netEaseCloudMusicChartMusicInfoList;
        private ViewHolder viewHolder;

        public Adapter(List<NetEaseCloudMusicChartMusicInfo> netEaseCloudMusicChartMusicInfoList) {
            this.netEaseCloudMusicChartMusicInfoList = netEaseCloudMusicChartMusicInfoList;
        }

        @Override
        public int getCount() {
            return netEaseCloudMusicChartMusicInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return netEaseCloudMusicChartMusicInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_music_chart_detail_lv, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            NetEaseCloudMusicChartMusicInfo itemData = netEaseCloudMusicChartMusicInfoList.get(position);
            viewHolder.musicChartMusicSortTv.setText(String.valueOf(itemData.getSort()));
            Glide.with(context).asBitmap().load(itemData.getImg()).transition(withCrossFade()).into(viewHolder.musicChartMusicImg);
            viewHolder.musicChartMusicNameTv.setText(itemData.getName());
            viewHolder.musicChartMusicInfoTv.setText(itemData.getAlias());
            viewHolder.musicChartMusicSongersTv.setText(itemData.getArtistNames());
            int durcation = itemData.getDuration() / 1000;
            String timeDurcation;
            if (durcation < 3600) {
                timeDurcation = String.format("%1$02d:%2$02d", durcation / 60, durcation % 60);
            } else {
                timeDurcation = String.format("%1$d:%2$02d:%3$02d", durcation / 3600, durcation % 3600 / 60, durcation % 60);
            }
            viewHolder.musicChartMusicDurationTv.setText(timeDurcation);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.music_chart_music_sort_tv)
            TextView musicChartMusicSortTv;
            @BindView(R.id.music_chart_music_img)
            ImageView musicChartMusicImg;
            @BindView(R.id.music_chart_music_name_tv)
            TextView musicChartMusicNameTv;
            @BindView(R.id.music_chart_music_info_tv)
            TextView musicChartMusicInfoTv;
            @BindView(R.id.music_chart_music_songers_tv)
            TextView musicChartMusicSongersTv;
            @BindView(R.id.music_chart_music_duration_tv)
            TextView musicChartMusicDurationTv;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mediaPlayer.stop();
            mediaPlayer = null;
        } catch (Exception e) {

        }
    }
}
