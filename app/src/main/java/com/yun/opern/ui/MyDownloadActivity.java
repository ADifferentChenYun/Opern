package com.yun.opern.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.model.OpernImgInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.utils.CacheFileUtil;
import com.yun.opern.views.ActionBarNormal;
import com.yun.opern.views.SquareImageView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyDownloadActivity extends AppCompatActivity {
    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.img_gv)
    GridView imgGv;
    private ArrayList<OpernInfo> opernInfos = new ArrayList<>();
    private GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_download);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        new ScanImgFileThread(new ScanImgFileThread.CallBack() {
            @Override
            public void onFinish(ArrayList<OpernInfo> opernInfoList) {
                opernInfos.clear();
                opernInfos.addAll(opernInfoList);
                adapter.notifyDataSetChanged();
            }
        }).execute();
        adapter = new GridViewAdapter(opernInfos);
        imgGv.setAdapter(adapter);
    }

    public class GridViewAdapter extends BaseAdapter {
        private ArrayList<OpernInfo> opernInfos;
        private ViewHolder viewHolder;

        public GridViewAdapter(ArrayList<OpernInfo> opernInfos) {
            this.opernInfos = opernInfos;
        }

        @Override
        public int getCount() {
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_img_gv_layout, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OpernInfo opernInfo = opernInfos.get(position);
            viewHolder.itemImgGvLayoutTv.setText(opernInfo.getTitle());
            Glide.with(MyDownloadActivity.this).load(opernInfo.getImgs().get(0).getOpernImg()).into(viewHolder.itemImgGvLayoutImg);
            return convertView;
        }


        public class ViewHolder {
            @BindView(R.id.item_img_gv_layout_img)
            SquareImageView itemImgGvLayoutImg;
            @BindView(R.id.item_img_gv_layout_tv)
            TextView itemImgGvLayoutTv;

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
                        opernImgInfo.setId(imgFile.getName());
                        opernImgInfo.setOpernId(Integer.parseInt(imgFile.getName().split("_")[0]));
                        opernImgInfo.setOpernIndex(Integer.parseInt(imgFile.getName().split("_")[1]));
                        opernImgInfo.setOpernTitle(childFile.getName());
                        opernImgInfo.setOpernImg(imgFile.getPath());
                        opernImgInfos.add(opernImgInfo);
                    }
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
