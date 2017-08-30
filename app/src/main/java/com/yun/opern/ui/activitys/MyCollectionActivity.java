package com.yun.opern.ui.activitys;

import com.yun.opern.R;
import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.common.WeiBoUserInfoKeeper;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.net.HttpCore;
import com.yun.opern.ui.bases.BaseActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCollectionActivity extends BaseActivity {


    @Override
    protected int contentViewRes() {
        return R.layout.activity_my_collection;
    }

    @Override
    protected void initView() {
        getCollectedOpernInfo();
    }

    private void getCollectedOpernInfo(){
        WeiBoUserInfo weiBoUserInfo = WeiBoUserInfoKeeper.read(context);
        if(weiBoUserInfo == null){
            return;
        }
        HttpCore.getInstance().getApi().getCollectionOpernInfo(weiBoUserInfo.getId()).enqueue(new Callback<BaseResponse<ArrayList<OpernInfo>>>() {
            @Override
            public void onResponse(Call<BaseResponse<ArrayList<OpernInfo>>> call, Response<BaseResponse<ArrayList<OpernInfo>>> response) {
                response.body().getData().forEach(System.out::println);
            }

            @Override
            public void onFailure(Call<BaseResponse<ArrayList<OpernInfo>>> call, Throwable t) {

            }
        });
    }
}
