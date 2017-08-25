package com.yun.opern.net;

import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernInfo;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yun on 2017/8/10 0010.
 */
public interface Api {

    @GET(value = "opern/popularOpernInfo")
    Call<BaseResponse<ArrayList<OpernInfo>>> getPopOpernInfo(@Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "opern/searchOpernInfo")
    Call<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfo(@Query("searchParameter") String searchParameter);
}
