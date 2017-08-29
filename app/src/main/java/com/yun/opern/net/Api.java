package com.yun.opern.net;

import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.model.UserLoginRequestInfo;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Yun on 2017/8/10 0010.
 */
public interface Api {

    @GET(value = "https://api.weibo.com/2/users/show.json")
    Call<WeiBoUserInfo> getWeiBoUserInfo(@Query("access_token") String access_token, @Query("uid") String uid);

    @POST(value = "user/login")
    Call<BaseResponse> userLogin(@Body UserLoginRequestInfo info);

    @GET(value = "opern/popularOpernInfo")
    Call<BaseResponse<ArrayList<OpernInfo>>> getPopOpernInfo(@Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "opern/searchOpernInfo")
    Call<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfo(@Query("searchParameter") String searchParameter);
}
