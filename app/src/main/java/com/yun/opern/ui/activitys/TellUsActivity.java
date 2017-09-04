package com.yun.opern.ui.activitys;

import android.app.AlertDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.FeedbackInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.utils.T;
import com.yun.opern.views.ActionBarNormal;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class TellUsActivity extends BaseActivity {


    @BindView(R.id.actionbar)
    ActionBarNormal actionbar;
    @BindView(R.id.feedback_rv)
    RecyclerView feedbackRv;
    @BindView(R.id.add_feedback_fab)
    FloatingActionButton addFeedbackFab;
    @BindView(R.id.empty_view)
    View emptyView;

    private RecyclerViewAdapter adapter;
    private ArrayList<FeedbackInfo> feedbackInfos = new ArrayList<>();

    @Override
    protected int contentViewRes() {
        return R.layout.activity_tell_us;
    }

    @Override
    protected void initView() {
        adapter = new RecyclerViewAdapter(feedbackInfos);
        feedbackRv.setLayoutManager(new LinearLayoutManager(context));
        feedbackRv.setAdapter(adapter);
    }

    @Override
    protected void initedView() {
        super.initedView();
        getFeedbackInfos();
    }

    private void getFeedbackInfos() {
        showProgressDialog(true);
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        HttpCore.getInstance().getApi().getFeedbackInfos(weiBoUserInfo.getId())
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(arrayListBaseResponse -> {
                    feedbackInfos.clear();
                    feedbackInfos.addAll(arrayListBaseResponse.getData());
                    adapter.notifyDataSetChanged();
                    feedbackRv.scrollToPosition(feedbackInfos.size() - 1);
                    showProgressDialog(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    T.showShort(throwable.getMessage());
                    showProgressDialog(false);
                });
    }

    private void commitFeedbackInfo(String feedbackMessage) {
        showProgressDialog(true);
        FeedbackInfo feedbackInfo = new FeedbackInfo();
        feedbackInfo.setUserId(WeiBoUserInfoKeeper.read(context).getId());
        feedbackInfo.setFeedbackMessage(feedbackMessage);
        HttpCore.getInstance().getApi().commitFeedback(feedbackInfo)
                .subscribeOn(new NewThreadScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(baseResponse -> {
                    getFeedbackInfos();
                    showProgressDialog(false);
                }, throwable -> {
                    throwable.printStackTrace();
                    T.showShort(throwable.getMessage());
                    showProgressDialog(false);
                });
    }

    @OnClick(R.id.add_feedback_fab)
    void OnAddFeedbackFabClick() {
        View view = View.inflate(context, R.layout.layout_input_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view)
                .setTitle("输入反馈信息")
                .setPositiveButton("提交", (dialog, which) -> commitFeedbackInfo(((EditText) view.findViewById(R.id.layout_input_dialog_edt)).getText().toString()))
                .create();
        alertDialog.show();
    }


    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private ArrayList<FeedbackInfo> feedbackInfos;

        public RecyclerViewAdapter(ArrayList<FeedbackInfo> feedbackInfos) {
            this.feedbackInfos = feedbackInfos;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_feedback_rv, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.feedbackMessageTv.setText(feedbackInfos.get(position).getFeedbackMessage());
            holder.feedbackTimeTv.setText(feedbackInfos.get(position).getFeedbackDateTime());
            Glide.with(context).asBitmap().load(WeiBoUserInfoKeeper.read(context).getAvatar_hd()).transition(withCrossFade()).into(holder.userHeadImg);
        }

        @Override
        public int getItemCount() {
            emptyView.setVisibility(feedbackInfos.size() == 0 ? View.VISIBLE : View.GONE);
            return feedbackInfos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView userHeadImg;
            private TextView feedbackMessageTv;
            private TextView feedbackTimeTv;

            public ViewHolder(View itemView) {
                super(itemView);
                userHeadImg = (CircleImageView) itemView.findViewById(R.id.user_head_img);
                feedbackMessageTv = (TextView) itemView.findViewById(R.id.item_feedback_rv_msg_tv);
                feedbackTimeTv = (TextView) itemView.findViewById(R.id.item_feedback_rv_time_tv);
            }
        }
    }
}
