package com.yun.opern.ui.activitys;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.model.event.OpernFileDeleteEvent;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.utils.ErrorMessageUtil;
import com.yun.opern.utils.FileUtil;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SquareImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class MyDownloadActivity extends BaseActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.img_gv)
    GridView imgGv;
    @BindView(R.id.empty_view)
    View emptyView;

    private ArrayList<OpernInfo> opernInfos = new ArrayList<>();
    private GridViewAdapter adapter;


    @Override
    protected int contentViewRes() {
        return R.layout.activity_my_download;
    }

    @Override
    protected void initView() {
        new ScanImgFileThread(opernInfoList -> {
            opernInfos.clear();
            opernInfos.addAll(opernInfoList);
            adapter.notifyDataSetChanged();
        }).execute();
        adapter = new GridViewAdapter(opernInfos);
        imgGv.setAdapter(adapter);
        imgGv.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, ShowImageActivity.class);
            intent.putExtra("opernInfo", opernInfos.get(position));
            startActivity(intent);
        });
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
            Glide.with(MyDownloadActivity.this).asBitmap().load(opernInfo.getImgs().get(0).getOpernImg()).transition(withCrossFade()).into(viewHolder.itemImgGvLayoutImg);
            viewHolder.deleteImg.setOnClickListener(v -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("删除本地曲谱")
                        .setMessage("删除后本地就找不到了哦~")
                        .setPositiveButton("删除", (dialog, which) -> {
                            boolean delete = FileUtil.deleteLocalOpernImgs(opernInfo);
                            if (delete) {
                                opernInfos.remove(position);
                                notifyDataSetChanged();
                                EventBus.getDefault().post(new OpernFileDeleteEvent());
                            } else {
                                ErrorMessageUtil.showErrorByToast("删除失败");
                            }
                        })
                        .setCancelable(true)
                        .create();
                alertDialog.show();
            });
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

    private static class ScanImgFileThread extends AsyncTask<Void, Void, ArrayList<OpernInfo>> {
        private CallBack callBack;

        public ScanImgFileThread(CallBack callBack) {
            this.callBack = callBack;
        }

        interface CallBack {
            void onFinish(ArrayList<OpernInfo> opernInfoList);
        }

        @Override
        protected ArrayList<OpernInfo> doInBackground(Void... params) {
            File file = new File(CacheFileUtil.cacheFilePath);
            ArrayList<OpernInfo> opernInfos = new ArrayList<>();
            try {
                for (File childFile : file.listFiles()) {
                    OpernInfo opernInfo = new OpernInfo();
                    opernInfo.setTitle(childFile.getName());
                    ArrayList<OpernImgInfo> opernImgInfos = new ArrayList<>();
                    opernInfo.setImgs(opernImgInfos);
                    opernInfos.add(opernInfo);
                    for (File imgFile : childFile.listFiles()) {
                        OpernImgInfo opernImgInfo = new OpernImgInfo();
                        String id = imgFile.getName().split("\\.")[0];
                        opernImgInfo.setId(id);
                        opernImgInfo.setOpernId(Integer.parseInt(id.split("_")[0]));
                        opernImgInfo.setOpernIndex(Integer.parseInt(id.split("_")[1]));
                        opernImgInfo.setOpernTitle(childFile.getName());
                        opernImgInfo.setOpernImg(imgFile.getPath());
                        opernImgInfos.add(opernImgInfo);
                    }
                    Collections.sort(opernImgInfos, (o1, o2) -> {
                        if (o1.getOpernIndex() > o2.getOpernIndex()) {
                            return 1;
                        }
                        if (o1.getOpernIndex() < o2.getOpernIndex()) {
                            return -1;
                        }
                        return 0;
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return opernInfos;
        }

        @Override
        protected void onPostExecute(ArrayList<OpernInfo> opernInfos) {
            super.onPostExecute(opernInfos);
            callBack.onFinish(opernInfos);
        }
    }
}
