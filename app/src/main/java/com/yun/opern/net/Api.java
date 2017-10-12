package com.yun.opern.net;

import com.yun.opern.common.WeiBoUserInfo;
import com.yun.opern.model.BaseResponse;
import com.yun.opern.model.CategoryInfo;
import com.yun.opern.model.FeedbackInfo;
import com.yun.opern.model.OpernInfo;
import com.yun.opern.model.UpdateInfo;
import com.yun.opern.model.UserLoginRequestInfo;
import java.math.BigInteger;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Yun on 2017/8/10 0010.
 */
public interface Api {

    @GET(value = "update/checkVersion")
    Observable<BaseResponse<UpdateInfo>> checkVersion();

    @GET(value = "https://api.weibo.com/2/users/show.json")
    Observable<WeiBoUserInfo> getWeiBoUserInfo(@Query("access_token") String access_token, @Query("uid") String uid);

    @POST(value = "user/login")
    Observable<BaseResponse> userLogin(@Body UserLoginRequestInfo info);

    @GET(value = "opern/popularOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getPopOpernInfo(@Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "opern/searchOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfo(@Query("searchParameter") String searchParameter);

    @GET(value = "opern/searchOpernInfoByCategory")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfoByCategory(@Query("categoryOne") String categoryOne, @Query("categoryTwo") String categoryTwo, @Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "collection/isCollected")
    Observable<BaseResponse> isCollected(@Query("userId") BigInteger userId, @Query("opernId") int opernId);

    @GET(value = "collection/add")
    Observable<BaseResponse> addCollection(@Query("userId") BigInteger userId, @Query("opernId") int opernId);

    @GET(value = "collection/remove")
    Observable<BaseResponse> removeCollection(@Query("userId") BigInteger userId, @Query("opernId") int opernId);

    @GET(value = "collection/collectionOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getCollectionOpernInfo(@Query("userId") BigInteger userId);

    @POST(value = "feedback/commit")
    Observable<BaseResponse> commitFeedback(@Body FeedbackInfo feedbackInfo);

    @GET(value = "feedback/getFeedbackInfos")
    Observable<BaseResponse<ArrayList<FeedbackInfo>>> getFeedbackInfos(@Query("userId") BigInteger userId);

    @GET(value = "category/categoryInfo")
    Observable<BaseResponse<ArrayList<CategoryInfo>>> getCategoryInfo();

    @GET(value = "opern/latestUpdateTime")
    Observable<BaseResponse<String>> latestUpdateTime();

    @GET(value = "opern/latestUpdateOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> latestUpdateOpernInfo();
}
